package org.exoplatform.mandatoryspaces;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

/**
 * @author <a href="mailto:obouras@exoplatform.com">Omar Bouras</a>
 * @version ${Revision}
 * @date 21/09/16
 */

public class AddInMandatorySpaceOnConnectListener extends Listener<ConversationRegistry, ConversationState> {

    private static final Log LOG = ExoLogger.getLogger(AddInMandatorySpaceOnConnectListener.class);

    private List<String> defaultSpaceName;


    public AddInMandatorySpaceOnConnectListener(InitParams params, SpaceService spaceService){
        defaultSpaceName=new ArrayList<String>();
        String spaces= params.getValueParam("defaultSpaces").getValue();
        if (!spaces.equals("${exo.addons.mandatorySpaces}")) {
            String[] spacesTab = spaces.split(",");
            for (String spaceName : spacesTab) {
                if (!spaceName.equals("")) defaultSpaceName.add(spaceName.trim());
            }
        }
    }


    @Override
    public void onEvent(Event<ConversationRegistry, ConversationState> event) throws Exception {
        String userId = event.getData().getIdentity().getUserId();
        ExoContainer container = ExoContainerContext.getCurrentContainer();
        SpaceService spaceService = (SpaceService) container.getComponentInstanceOfType(SpaceService.class);
        try {
            RequestLifeCycle.begin(container);
            for (String spaceName : defaultSpaceName) {
                try {
                    Space space = spaceService.getSpaceByGroupId("/spaces/" + spaceName);
                    if (space!=null) {
                        if (!spaceService.isMember(space, userId)) {
                            spaceService.addMember(space, userId);
                        }
                    }
                } catch (Exception e) {
                    LOG.warn("Failed to add user {} in space {} : ", userId, spaceName, e);
                }

            }

        } finally {
            RequestLifeCycle.end();
        }
    }
}
