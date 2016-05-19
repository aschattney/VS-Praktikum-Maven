package de.hochschuledarmstadt.config;

public class CredentialParser {

    private CredentialParser() {}

    /**
     * Parses the credentials from the command line arguments {@code commandLineArgs}
     * @param module {@link Credential#PROTOCOL_TCP or Credential#PROTOCOL_UDP}
     * @param commandLineArgs the command line arguments
     * @param defaultIp will be used as part of a default credential if no credential is provided from the command line
     * @param defaultPort will be used as part of a default credential if no credential is provided from the command line
     * @return {@link Credential}
     */
    public static Credential parse(String module, String[] commandLineArgs, String defaultProtocol, String defaultIp, int defaultPort) {

        if (!defaultProtocol.equals(Credential.PROTOCOL_TCP) && !defaultProtocol.equals(Credential.PROTOCOL_UDP))
            throw new IllegalArgumentException("Unknown Transport Protocol!");

        for (String command : commandLineArgs){
            try{
                String[] pair = command.split("=");
                String m = pair[0];
                String strCredentials = null;

                if (module == null && pair.length > 1)
                    continue;

                if (module == null){
                    strCredentials = pair[0];
                }else if(!m.equals(module))
                    continue;
                else if(pair.length == 2)
                    strCredentials = pair[1];

                String[] splittedCredentials = strCredentials.split("://");
                String protocol = splittedCredentials[0].toUpperCase();

                String[] ipAndProtocol = splittedCredentials[1].split(":");
                String ip = ipAndProtocol[0];
                int port = Integer.parseInt(ipAndProtocol[1]);
                System.out.println(String.format("IP:%s, Port:%s, Protocol:%s", ip, port, protocol));
                return new Credential(protocol, ip, port);

            }catch(Exception e){
                e.printStackTrace();
            }

        }
        return new Credential(defaultProtocol, defaultIp, defaultPort);
    }

    public static Credential parse(String[] commandLineArgs, String defaultProtocol, String defaultIp, int defaultPort) {
        return parse(null, commandLineArgs, defaultProtocol, defaultIp, defaultPort);
    }

}
