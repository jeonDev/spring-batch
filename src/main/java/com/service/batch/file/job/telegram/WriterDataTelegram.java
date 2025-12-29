package com.service.batch.file.job.telegram;

import com.service.batch.common.telegram.Telegram;

public record WriterDataTelegram(
        String dataA,
        String dataB
) implements Telegram {

}
