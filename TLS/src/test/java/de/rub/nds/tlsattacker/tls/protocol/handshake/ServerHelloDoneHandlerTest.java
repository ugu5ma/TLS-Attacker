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
package de.rub.nds.tlsattacker.tls.protocol.handshake;

import de.rub.nds.tlsattacker.tls.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.tls.workflow.TlsContext;
import de.rub.nds.tlsattacker.util.ArrayConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Juraj Somorovsky - juraj.somorovsky@rub.de
 * @author Florian Pfützenreuter - florian.pfuetzenreuter@rub.de
 * @author Philip Riese <philip.riese@rub.de>
 */
public class ServerHelloDoneHandlerTest {

    private ServerHelloDoneHandler handler;

    public ServerHelloDoneHandlerTest() {
	handler = new ServerHelloDoneHandler(new TlsContext());
    }

    /**
     * Test of prepareMessageAction method, of class ServerHelloDoneHandler.
     */
    @Test
    public void testPrepareMessageAction() {
	handler.setProtocolMessage(new ServerHelloDoneMessage());

	ServerHelloDoneMessage message = (ServerHelloDoneMessage) handler.getProtocolMessage();

	byte[] returned = handler.prepareMessageAction();
	byte[] expected = ArrayConverter.concatenate(new byte[] { HandshakeMessageType.SERVER_HELLO_DONE.getValue() },
		new byte[] { 0x00, 0x00, 0x00 });

	assertNotNull("Confirm function didn't return 'NULL'", returned);
	assertArrayEquals("Confirm returned message equals the expected message", expected, returned);
    }

    /**
     * Test of parseMessageAction method, of class ServerHelloDoneHandler.
     */
    @Test
    public void testParseMessageAction() {
	byte[] serverHelloDoneMsg = { 0x0e, 0x00, 0x00, 0x00 };
	handler.initializeProtocolMessage();

	int endPointer = handler.parseMessage(serverHelloDoneMsg, 0);
	ServerHelloDoneMessage message = handler.getProtocolMessage();

	assertNotNull("Confirm that parseMessage didn't return 'NULL'", endPointer);
	assertEquals("Confirm expected message type: \"ServerHelloDone\"", HandshakeMessageType.SERVER_HELLO_DONE,
		message.getHandshakeMessageType());
	assertEquals("Confirm expected message length of \"0\"", new Integer(0), message.getLength().getValue());
	assertEquals("Confirm the correct value of endPointer representing the " + "actual number of message bytes",
		serverHelloDoneMsg.length, endPointer);
    }

}
