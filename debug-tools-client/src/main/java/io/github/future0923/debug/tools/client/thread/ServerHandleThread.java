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
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.PacketCodec;

/**
 * @author future0923
 */
public class ServerHandleThread extends Thread {

    private static final Logger logger = Logger.getLogger(ServerHandleThread.class);

    private final ClientSocketHolder holder;

    private final PacketHandleService packetHandleService;

    public ServerHandleThread(ClientSocketHolder holder, PacketHandleService packetHandleService) {
        setDaemon(true);
        setName("DebugTools-ServerHandle-Thread-" + holder.getConfig().getPort());
        this.holder = holder;
        this.packetHandleService = packetHandleService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (holder.isClosed()) {
                logger.warning("debug tools client disconnect the link, waiting to reconnect");
                break;
            }
            try {
                Packet packet = PacketCodec.INSTANCE.getPacket(holder.getInputStream());
                if (packet != null) {
                    packetHandleService.handle(holder.getOutputStream(), packet);
                }
            } catch (Exception e) {
                logger.error("socket io error :{} , error:{}", holder.getSocket(), e);
                break;
            }
        }
    }
}