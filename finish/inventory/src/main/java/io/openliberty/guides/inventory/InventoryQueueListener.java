package io.openliberty.guides.inventory;

import io.openliberty.guides.models.SystemLoad;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import java.util.logging.Logger;

@MessageDriven(mappedName="jms/InventoryQueue")
public class InventoryQueueListener implements MessageListener {
    private static Logger logger = Logger.getLogger(InventoryQueueListener.class.getName());
    @Inject
    private InventoryManager manager;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                SystemLoad systemLoad = (SystemLoad) objMessage.getObject();

                String hostname = systemLoad.hostname;
                Double loadAverage = systemLoad.loadAverage;

                if (manager.getSystem(hostname).isPresent()) {
                    manager.updateCpuStatus(hostname, loadAverage);
                    logger.info("Host " + hostname + " was updated: " + loadAverage);
                } else {
                    manager.addSystem(hostname, loadAverage);
                    logger.info("Host " + hostname + " was added: " + loadAverage);
                }
            }
        } catch (JMSException e) {
            logger.info("JMSError" + e.getMessage());
        }
    }
}
