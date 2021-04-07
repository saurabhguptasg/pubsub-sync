# pubsub-sunc

## Introduction

In systems like Cloud Run, background processing is severely throttled, and this can cause issues with
running the default pubsub library to publish messages *from* Cloud Run *to* PubSub.

This code snippet shows how to use the REST API along with `GoogleCredentials`
to synchronously transmit messages to PubSub and not get throttled by the background
process limitations.

Do note that since this is a synchronous operation, you'll have to execute this
in the scope of the HTTP reqeuest that Cloud Run will be processing. This can
increase the latency of your request.

### License

pubsub-sync is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2021 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### Disclaimer

This is not an officially supported Google product.