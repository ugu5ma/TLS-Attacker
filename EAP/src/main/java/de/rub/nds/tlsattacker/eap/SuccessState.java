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
package de.rub.nds.tlsattacker.eap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Throws Success Message, if EAP-Frame Success was received.
 * 
 * @author Felix Lange <flx.lange@gmail.com>
 */
public class SuccessState implements EapState {

    private static final Logger LOGGER = LogManager.getLogger(FailureState.class);

    EapolMachine eapolMachine;

    int id;

    EapFactory eaptlsfactory = new EapTlsFactory();

    NetworkHandler nic = NetworkHandler.getInstance();

    public SuccessState(EapolMachine eapolMachine, int id) {

	this.eapolMachine = eapolMachine;
	this.id = id;

	nic.closeCon();
	LOGGER.info("Success, Connection permit!");
	// System.exit(0);

    }

    @Override
    public void send() {

    }

    @Override
    public void sendTLS(byte[] tlspacket) {

    }

    @Override
    public byte[] receive() {
	return null;
    }

    public String getState() {
	return "SuccessState";
    }

    @Override
    public int getID() {

	return 0;

    }

}
