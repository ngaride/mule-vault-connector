package com.avioconsulting.mule.connector.vault.provider.internal.connection.provider;

import com.avioconsulting.mule.connector.vault.provider.internal.connection.VaultConnection;
import com.avioconsulting.mule.connector.vault.provider.internal.connection.impl.TLSVaultConnection;
import com.avioconsulting.mule.connector.vault.provider.api.parameter.*;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides {@link TLSVaultConnection} instances and the functionality to disconnect and validate those
 * connections. This is a {@link PoolingConnectionProvider} which will pool and reuse connections.
 */
@DisplayName("TLS Connection")
@Alias("tls-connection")
public class VaultTLSConnectionProvider implements PoolingConnectionProvider<VaultConnection> {

    private static final Logger logger = LoggerFactory.getLogger(VaultTLSConnectionProvider.class);

    @DisplayName("Vault URL")
    @Parameter
    private String vaultUrl;

    @DisplayName("Secrets Engine Version")
    @Parameter
    @Optional
    private EngineVersion engineVersion;

    @ParameterGroup(name="TLS Authentication Parameters")
    private TLSAuthProperties tlsAuthProperties;

    @DisplayName("SSL Properties")
    @Parameter
    @Optional
    @Placement(tab = Placement.CONNECTION_TAB)
    private SSLProperties sslProperties;

    @Override
    public VaultConnection connect() throws ConnectionException {
        return new TLSVaultConnection("tls_conn",
                vaultUrl,
                tlsAuthProperties.getJksProperties(),
                tlsAuthProperties.getPemProperties(),
                sslProperties,
                engineVersion);
    }

    @Override
    public void disconnect(VaultConnection connection) {
        try {
            connection.invalidate();
        } catch (Exception e) {
            logger.error("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage(), e);
        }
    }

    @Override
    public ConnectionValidationResult validate(VaultConnection connection) {
        if (connection.isValid()) {
            return ConnectionValidationResult.success();
        } else {
            return ConnectionValidationResult.failure("Connection Invalid", null);
        }

    }

}