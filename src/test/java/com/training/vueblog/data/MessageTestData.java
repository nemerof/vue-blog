package com.training.vueblog.data;

import static com.training.vueblog.data.TagTestData.TAG1;
import static com.training.vueblog.data.TagTestData.TAG2;
import static com.training.vueblog.data.UserTestData.ADMIN;
import static com.training.vueblog.data.UserTestData.USER;

import com.training.vueblog.objects.Message;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class MessageTestData {

  public static final Message MESSAGE1 = new Message(
    "a5911a57-f116-472b-a39e-d5ce26658494", "Body 1",
    LocalDateTime.of(2021,10,11,10,21,34,736412),
    "https://storage.googleapis.com/vueblog-files-bucket/380fe312-cb46-416e-9538-d8b4bc6b5920.jpg",
    USER, Arrays.asList(TAG1, TAG2)
  );

  public static final Message MESSAGE2 = new Message(
    "cb8c6676-7392-4bc4-9085-cd435ce29c4b", "Body 2",
    LocalDateTime.of(2021,11,9,16,34,13,437236),
    ADMIN, List.of(TAG2)
  );
}
