package com.service.batch.file.job.telegram;

import com.service.batch.common.telegram.FixedLength;
import com.service.batch.common.telegram.Telegram;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FixedLengthDataTelegram implements Telegram {
    @FixedLength(length = 1) private String type;
    @FixedLength(length = 5) private String data;
    @FixedLength(length = 5) private String data2;
    @FixedLength(length = 5) private String data3;
}
