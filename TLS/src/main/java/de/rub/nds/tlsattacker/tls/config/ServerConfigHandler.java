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
package de.rub.nds.tlsattacker.tls.config;

import de.rub.nds.tlsattacker.tls.constants.ConnectionEnd;
import de.rub.nds.tlsattacker.tls.exceptions.ConfigurationException;
import de.rub.nds.tlsattacker.tls.workflow.TlsContext;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowExecutor;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowExecutorFactory;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowConfigurationFactory;
import de.rub.nds.tlsattacker.tls.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.transport.TransportHandler;
import de.rub.nds.tlsattacker.transport.TransportHandlerFactory;
import de.rub.nds.tlsattacker.util.KeystoreHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Philip Riese <philip.riese@rub.de>
 */
public class ServerConfigHandler extends ConfigHandler {

    private static Logger LOGGER = LogManager.getLogger(ServerConfigHandler.class);

    @Override
    public TransportHandler initializeTransportHandler(CommandConfig config) throws ConfigurationException {
	ServerCommandConfig ccConfig = (ServerCommandConfig) config;
	TransportHandler th = TransportHandlerFactory.createTransportHandler(config.getTransportHandlerType(),
		config.getTlsTimeout());
	try {
	    String host = "server";
	    int port = Integer.parseInt(ccConfig.getPort());
	    th.initialize(host, port);
	    return th;
	} catch (ArrayIndexOutOfBoundsException | NullPointerException | NumberFormatException ex) {
	    throw new ConfigurationException(ccConfig.getPort() + " is an invalid string for host:port configuration",
		    ex);
	} catch (IOException ex) {
	    throw new ConfigurationException("Unable to initialize the transport handler with: " + ccConfig.getPort(),
		    ex);
	}
    }

    @Override
    public TlsContext initializeTlsContext(CommandConfig config) {
	ServerCommandConfig ccConfig = (ServerCommandConfig) config;
	TlsContext tlsContext;
	if (ccConfig.getWorkflowTraceConfigFile() != null) {
	    try {
		tlsContext = new TlsContext();
		FileInputStream fis = new FileInputStream(ccConfig.getWorkflowTraceConfigFile());
		WorkflowTrace workflowTrace = WorkflowTraceSerializer.read(fis);
		tlsContext.setWorkflowTrace(workflowTrace);
	    } catch (IOException | JAXBException | XMLStreamException ex) {
		throw new ConfigurationException("The workflow trace could not be loaded from "
			+ ccConfig.getWorkflowTraceConfigFile(), ex);
	    }
	} else {
	    WorkflowConfigurationFactory factory = WorkflowConfigurationFactory.createInstance(config);
	    switch (ccConfig.getWorkflowTraceType()) {
		case FULL:
		    tlsContext = factory.createFullTlsContext();
		    break;
		case HANDSHAKE:
		    tlsContext = factory.createHandshakeTlsContext();
		    break;
		case CLIENT_HELLO:
		    tlsContext = factory.createClientHelloTlsContext();
		    break;
		default:
		    throw new ConfigurationException("not supported workflow type: " + ccConfig.getWorkflowTraceType());
	    }

	}
	tlsContext.setMyConnectionEnd(ConnectionEnd.SERVER);

	if (config.isClientAuthentication()) {
	    tlsContext.setClientAuthentication(true);
	}
	if (config.getKeystore() != null) {
	    try {
		KeyStore ks = KeystoreHandler.loadKeyStore(config.getKeystore(), config.getPassword());
		tlsContext.setKeyStore(ks);
		tlsContext.setAlias(config.getAlias());
		tlsContext.setPassword(config.getPassword());
		if (LOGGER.isDebugEnabled()) {
		    Enumeration<String> aliases = ks.aliases();
		    LOGGER.debug("Successfully read keystore with the following aliases: ");
		    while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			LOGGER.debug("  {}", alias);
		    }
		}
	    } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex) {
		LOGGER.error(ex);
		throw new ConfigurationException(ex.getLocalizedMessage(), ex);
	    }
	}

	return tlsContext;
    }

    @Override
    public WorkflowExecutor initializeWorkflowExecutor(TransportHandler transportHandler, TlsContext tlsContext) {
	WorkflowExecutor executor = WorkflowExecutorFactory.createWorkflowExecutor(transportHandler, tlsContext);
	return executor;
    }

}
