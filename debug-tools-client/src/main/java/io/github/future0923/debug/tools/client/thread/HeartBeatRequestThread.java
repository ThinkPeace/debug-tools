/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.client.thread;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsThreadUtils;
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class HeartBeatRequestThread extends Thread {

    private static final Logger logger = Logger.getLogger(HeartBeatRequestThread.class);

    private final ClientSocketHolder holder;

    private final int interval;

    public HeartBeatRequestThread(ClientSocketHolder holder, int interval) {
        setDaemon(true);
        setName("DebugTools-HeartBeatRequest-Thread");
        this.holder = holder;
        this.interval = interval;
    }

    @Override
    public void run() {
        int retryCount = 0;
        while (!Thread.currentThread().isInterrupted() && retryCount < 20) {
            if (!DebugToolsThreadUtils.sleep(this.interval, TimeUnit.SECONDS)) {
                return;
            }
            if (!holder.isClosed()) {
                try {
                    HeartBeatRequestPacket.INSTANCE.writeAndFlush(holder.getOutputStream());
                    retryCount = 0;
                    holder.setRetry(ClientSocketHolder.INIT);
                    continue;
                } catch (IOException e) {
                    holder.closeSocket();
                    logger.error("HeartBeatRequest happen error", e);
                }
            }
            logger.warning("HeartBeatRequest reconnect debug tools server");
            holder.setRetry(ClientSocketHolder.RETRYING);
            try {
                holder.connect();
            } catch (IOException e) {
                logger.error("connect server error, Try again in {} seconds.", e, this.interval);
            }
            retryCount++;
        }
        holder.setRetry(ClientSocketHolder.FAIL);
    }
}