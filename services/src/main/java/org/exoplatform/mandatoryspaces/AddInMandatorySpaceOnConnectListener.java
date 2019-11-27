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
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

/**
 * @author <a href="mailto:rdenarie@exoplatform.com">Romain Dénarié</a>
 * @version ${Revision}
 */

public class AddInMandatorySpaceOnConnectListener extends Listener<ConversationRegistry, ConversationState> {

    private static final Log LOG = ExoLogger.getLogger(AddInMandatorySpaceOnConnectListener.class);

    private List<String> defaultSpaceName;

    private List<String> excludedGroups;

    private SpaceService spaceService;

    private OrganizationService organizationService;


    public AddInMandatorySpaceOnConnectListener(InitParams params, SpaceService spaceService, OrganizationService organizationService){
        this.spaceService=spaceService;
        this.organizationService=organizationService;

        defaultSpaceName=new ArrayList<String>();
        String spaces= params.getValueParam("defaultSpaces").getValue();
        if (!spaces.equals("${exo.addons.mandatorySpaces}")) {
            String[] spacesTab = spaces.split(",");
            for (String spaceName : spacesTab) {
                if (!spaceName.equals("")) defaultSpaceName.add(spaceName.trim());
            }
        }
        excludedGroups=new ArrayList<String>();
        String groups= params.getValueParam("excludedGroups").getValue();
        if (!groups.equals("${exo.addons.excludedGroups}")) {
            String[] groupsTab = groups.split(",");
            for (String groupName : groupsTab) {
                if (!groupName.equals("")) excludedGroups.add(groupName.trim());
            }
        }
    }


    @Override
    public void onEvent(Event<ConversationRegistry, ConversationState> event) throws Exception {
        String userId = event.getData().getIdentity().getUserId();
        ExoContainer container = ExoContainerContext.getCurrentContainer();
        if (!isInExcludedGroups(userId)) {
            try {
                RequestLifeCycle.begin(container);
                for (String spaceName : defaultSpaceName) {
                    try {
                        Space space = spaceService.getSpaceByGroupId("/spaces/" + spaceName);
                        if (space != null) {
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

    private boolean isInExcludedGroups(String userId) {
        MembershipHandler membershipHandler = this.organizationService.getMembershipHandler();
        for (String group : excludedGroups) {
            try {
                if (membershipHandler.findMembershipsByUserAndGroup(userId, group).size()>0) {
                    return true;
                }
            } catch (Exception e) {
                LOG.error("Unable to read group "+group,e);
            }
        }
        return false;

    }
}
