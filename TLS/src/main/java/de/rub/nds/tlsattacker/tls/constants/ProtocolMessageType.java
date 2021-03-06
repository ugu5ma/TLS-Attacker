/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS.
 *
 * Copyright (C) 2015 Chair for Network and Data Security,
 *                    Ruhr University Bochum
 *                    (juraj.somorovsky@rub.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.rub.nds.tlsattacker.tls.constants;

import de.rub.nds.tlsattacker.tls.protocol.ProtocolMessageHandler;
import de.rub.nds.tlsattacker.tls.protocol.alert.AlertHandler;
import de.rub.nds.tlsattacker.tls.protocol.application.ApplicationHandler;
import de.rub.nds.tlsattacker.tls.protocol.ccs.ChangeCipherSpecHandler;
import de.rub.nds.tlsattacker.tls.protocol.heartbeat.HeartbeatHandler;
import de.rub.nds.tlsattacker.tls.workflow.TlsContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Juraj Somorovsky <juraj.somorovsky@rub.de>
 */
public enum ProtocolMessageType {

    CHANGE_CIPHER_SPEC((byte) 20),
    ALERT((byte) 21),
    HANDSHAKE((byte) 22),
    APPLICATION_DATA((byte) 23),
    HEARTBEAT((byte) 24);

    private static final Logger LOGGER = LogManager.getLogger(ProtocolMessageType.class);

    private byte value;

    private static final Map<Byte, ProtocolMessageType> MAP;

    private ProtocolMessageType(byte value) {
	this.value = value;
    }

    static {
	MAP = new HashMap<>();
	for (ProtocolMessageType cm : ProtocolMessageType.values()) {
	    MAP.put(cm.value, cm);
	}
    }

    public static ProtocolMessageType getContentType(byte value) {
	return MAP.get(value);
    }

    public byte getValue() {
	return value;
    }

    public byte[] getArrayValue() {
	return new byte[] { value };
    }

    public ProtocolMessageHandler getProtocolMessageHandler(byte value, TlsContext tlsContext) {
	ProtocolMessageHandler pmh = null;
	LOGGER.debug("Trying to get a protocol message handler for the following content type: {}", this);
	switch (this) {
	    case HANDSHAKE:
		HandshakeMessageType hmt = HandshakeMessageType.getMessageType(value);
		LOGGER.debug("Trying to get a protocol message handler for the following handshake message: {}", hmt);
		pmh = hmt.getProtocolMessageHandler(tlsContext);
		break;
	    case CHANGE_CIPHER_SPEC:
		pmh = new ChangeCipherSpecHandler(tlsContext);
		break;
	    case ALERT:
		pmh = new AlertHandler(tlsContext);
		break;
	    case APPLICATION_DATA:
		pmh = new ApplicationHandler(tlsContext);
		break;
	    case HEARTBEAT:
		pmh = new HeartbeatHandler(tlsContext);
		break;
	}
	if (pmh == null) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
	return pmh;
    }
}
