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

import com.google.cloud.Timestamp;
import com.google.gson.JsonObject;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PubsubSyncApplicationTests {

  @Autowired MessagePublisher messagePublisher;

  @Test
  void contextLoads() throws IOException {
    JsonObject request = new JsonObject();
    request.addProperty("timestamp", Timestamp.now().toString());
    JsonObject response = messagePublisher.sendMessage(request);
    System.out.println("response = " + response);
    assert response.getAsJsonArray("messageIds").size() == 1;
  }

}
