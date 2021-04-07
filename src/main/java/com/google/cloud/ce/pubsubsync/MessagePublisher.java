/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.ce.pubsubsync;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ServiceOptions;
import com.google.common.io.ByteSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {
  private final String projectId;
  private final GoogleCredentials googleCredentials;

  private final String pubsubEndpoint = "https://pubsub.googleapis.com/v1/";
  private final NetHttpTransport netHttpTransport;
  private final HttpRequestFactory requestFactory;


  @Value("${TOPIC_NAME}")
  private String topicName;

  private GenericUrl pubsubUrl;

  public MessagePublisher() throws IOException {
    this.projectId = ServiceOptions.getDefaultProjectId();
    this.googleCredentials = GoogleCredentials.getApplicationDefault();
    this.netHttpTransport = new NetHttpTransport();
    this.requestFactory = this.netHttpTransport.createRequestFactory();
  }

  @PostConstruct
  private void init() {
    String path = "projects/" + projectId + "/topics/" + topicName + ":publish";
    pubsubUrl = new GenericUrl(pubsubEndpoint + path);
  }

  public JsonObject sendMessage(JsonObject message) throws IOException {
    String messageStr = message.toString();
    String messageBase64 = Base64.encodeBase64URLSafeString(messageStr.getBytes(StandardCharsets.UTF_8));

    JsonObject pubsubMessage = new JsonObject();
    pubsubMessage.addProperty("data", messageBase64);

    JsonArray messageArray = new JsonArray();
    messageArray.add(pubsubMessage);

    JsonObject payload = new JsonObject();
    payload.add("messages", messageArray);
    String payloadStr = payload.toString();

    HttpContent content = new ByteArrayContent(null, payloadStr.getBytes(StandardCharsets.UTF_8));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType("application/json");

    googleCredentials.refreshIfExpired();
    headers.setAuthorization("Bearer " + googleCredentials.getAccessToken().getTokenValue());
    HttpResponse response = requestFactory.buildPostRequest(pubsubUrl, content).setHeaders(headers).execute();
    ByteSource byteSource = new ByteSource() {
      @Override
      public InputStream openStream() throws IOException {
        return response.getContent();
      }
    };
    String responseText = byteSource.asCharSource(StandardCharsets.UTF_8).read();
    JsonObject responseJson = JsonParser.parseString(responseText).getAsJsonObject();

    return responseJson;
  }
}
