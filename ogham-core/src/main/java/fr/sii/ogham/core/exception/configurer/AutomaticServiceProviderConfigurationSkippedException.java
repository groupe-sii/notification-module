package fr.sii.ogham.core.exception.configurer;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class AutomaticServiceProviderConfigurationSkippedException extends ConfigureException {
    private static final long serialVersionUID = SERIAL_VERSION_UID;

    public AutomaticServiceProviderConfigurationSkippedException(String message) {
        super(message);
    }
}