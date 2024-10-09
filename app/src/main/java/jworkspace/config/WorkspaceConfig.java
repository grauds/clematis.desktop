package jworkspace.config;

import org.springframework.context.annotation.Configuration;

import jworkspace.users.ProfilesManager;
import lombok.Getter;

@Configuration
@Getter
public class WorkspaceConfig {

    private final ProfilesManager profilesManager;

    public WorkspaceConfig(ProfilesManager profilesManager) {
        this.profilesManager = profilesManager;
    }
}
