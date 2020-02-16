package cm.softinovplus.mobilebiller.mtn.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by nkalla on 11/09/18.
 */

public class Utils {
    //Email Validation pattern
    public static final String REGEX_EMAIL =  "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$" ;

    //Fragments Tags
    public static final String LoginFragment = "LoginFragment";
    public static final String SignUpFragment = "SignUpFragment";
    public static final String ForgotPasswordFragment = "ForgotPasswordFragment";

    public static final String TAG_BOOT_BROADCAST_RECEIVER = "BOOT_BROADCAST_RECEIVER";
    public static final String TAG_BOOT_EXECUTE_SERVICE = "BOOT_BROADCAST_SERVICE";
    public static final String REMOTE_REGULAREXPRESSION = "remote_regular_expression";


    public static int NOTIFICATION_ID = 1;
    public static final String NAME = "name";
    public static final String LIBELE_PDV = "libele_pdv";

    public static final int RequestPermissionCode = 1;

    public static final String SMS_SENDERS = "sms_senders";

    public static final String PDV = "pdv";

    public static final long DATABASE_SIZE = 1000;


    public static final String sms_id = "sms_id";
    public static final String data = "data";

    public static final String HOST_IDENTITY_AND_ACCESS          = "https://mobilebiller.idea-cm.club:444/";
    public static final String KEYWORDPATTERN                    = "keywordpattern";
    public static final String TRANSACTIONTYPE                   = "transactiontype";
    public static final String REGULAREXPRESSION                 = "regularexpression";

    public static final String PROTOCOL_HOST_PORT                = "";
    public static final String USERID                            = "userid";
    public static final String FIRSTNAME                         = "firstname";
    public static final String LASTNAME                          = "lastname";
    public static final String EMAIL                             = "email";
    public static  final String USERNAME                         = "username";
    public static final String TENANT                            = "tenant";
    public static final String TENANT_ID                         = "tenantid";
    public static final String TENANT_NAME                       = "tenant_name";
    public static final String TENANT_DESCRIPTION                = "tenant_description";
    public static final String PARENT                            = "parent";
    public static  final String PASSWORD                         = "password";
    public static final String ROLES                             = "roles";
    public static final String ACCESS_TOKEN                      = "access_token";
    public static final String MOBILEBILLERACCOUNT               = "mobilebillercreditaccount";
    public static final String APP_AUTHENTICATION                = "APP_AUTHENTICATION";
    public static final String APP_CONFIGURAION                  = "APP_CONFIGURAION";
    public static final String APP_OTHER_CONFIGURAION            = "APP_OTHER_CONFIGURAION";
    public static final String APP_SERVICE_ACCESS                 = "APP_SERVICE_ACCESS";

    public static final String BROADCAST_RECEIVER_REGISTERED     = "BROADCAST_RECEIVER_REGISTERED";
    public static final String NOTIFICATION_CHANEL_ID            = "4289EE2E-B99C-11E8-B7C2-AC2B6EE888A2";
    public static final String OK                                = "ok";
    public static final String KO                                = "ko";
    public static final String TOKEN_TYPE                        = "token_type";
    public static final String EXPIRES_IN                        = "expires_in";
    public static final String REFRESH_TOKEN                     = "refresh_token";
    public static final String ERROR                             = "error";
    public static final String MESSAGE                           = "message";
    public static final String AUTHORIZATION                     = "Authorization";
    public static final String BEARER                            = "Bearer";
    public static final String CONTENT_TYPE                      = "Content-Type";
    public static final String APPLICATION_JSON                  = "application/json";
    public static final String PRIVACY_POLICY_ACCEPTED           = "accepted";
    public static final String MOBILEBILLER_PRIVACY_POLICY       = "http://idea-cm.club/regles-mobilebiller.html";
    public static final String LAST_SMS_ID                       = "last_sms_id";
    public static final String INIT                              = "INIT";
    public static final String DEFAULT_MAC_ADDRESS               = "default_mac_address_bluetooth_device";
    public static final String RESPONSE                          = "response";
    public static final String TENANTID                          = "tenantid";
    public static final String TICKET_END_POINT                  = "https://mobilebiller.idea-cm.club:447/api/receipts-android";
    public static final String BULK_TICKET_END_POINT             = "https://mobilebiller.idea-cm.club:447/api/bulk-receipts-android";
    public static final String TICKET_ACCESS_TOKEN               = "ticket_access_token";
    public static final int TICKET_CLIENT_ID                     = 2;
    public static final String TICKET_CLIENT_SECRET              = "rjvYRlBhvsj8LDibO5Lf975OPp3kGbF5a6bzEEOe";
    public static final String TICKET_GRANT_TYPE                 = "client_credentials";
    public static final String TICKET_ACCESS_TOKEN_END_POINT     = "https://mobilebiller.idea-cm.club:447/oauth/token";
    public static final String ROOT_URL_LOGIN                    = "https://mobilebiller.idea-cm.club:444/api/users/";
    public static final String LOGIN_ACCESS_TOKEN_URL            = HOST_IDENTITY_AND_ACCESS + "api/access-token";
    public static final String ROOT_URL_USER_TENANTS             = "https://mobilebiller.idea-cm.club/api/tenants/";
    public static final int CLIENT_ID                            = 3;
    public static final String CLIENT_SECRET                     = "va2vv9Q2Ps57W3xiUPSEMGOpFcOx64RP9mGwYx5A";
    public static final String PASSWORD_GRANT_TYPE               = "password";
    public static final String SIGNUP_URL                        = "https://mobilebiller.idea-cm.club:444/api/tenants-provisions";
    public static final String RAISON                            = "raison";
    public static final int WALLET_CLIENT_ID                     = 1;
    public static final String WALLET_CLIENT_SECRET              = "nxei6UNWAjW7oYV9DXWLVd18VTP9yfJmUC1usaxI";
    public static final String WALLET_GRANT_TYPE                 = "client_credentials";
    public static final String WALLET_ACCESS_TOKEN_END_POINT     = "https://mobilebiller.idea-cm.club:446/oauth/token";
    public static final String WALLET_ACCESS_TOKEN               = "wallet_access_token";
    public static final String HOST_WALLET                       = "https://mobilebiller.idea-cm.club:446/";

    public static final int CLIENT_CLIENT_ID                     = 4;
    public static final String CLIENT_CLIENT_SECRET              = "uWsbWpN82H01Ido7yMPhsYPH9vdZ5cHoA4pXaY97";
    public static final String CLIENT_GRANT_TYPE                 = "client_credentials";
    public static final String CLIENT_ACCESS_TOKEN_END_POINT     = "https://mobilebiller.idea-cm.club/oauth/token";
    public static final String CLIENT_ACCESS_TOKEN               = "client_access_token";
    public static final String HOST_CLIENT                       = "https://mobilebiller.idea-cm.club/";
    public static final String DEFAULT_TENANT_ID                 = "";
    public static final String DEFAULT_TENANT_NAME               = "MOBILE BILLER";
    public static final String HOST_SERVICE_ACCESS               = "https://mobilebiller.idea-cm.club:4445/";
    public static final String SERVICE_ACCESS                    = "SERVICE_ACCESS";
    public static final String PRINT_SERVICE_ID                  = "61e857c0-cecd-11e8-bdce-49267d475577";
    public static final String serviceid                         = "serviceid";
    public static final String HOSTNAME                          = "mobilebiller.idea-cm.club";
    public static final String CONNECTIVITY_ACTION               = "CONNECTIVITY_ACTION";
    public static final String PHONE                             = "phone";
    public static final String TAXPAYERNUMBER                    = "taxpayernumber";
    public static final String NUMBERTRADEREGISTER               = "numbertraderegister";
    public static final String NUMREGISTRECOMMERCE               = "numregistrecommerce";
    public static final String NUMCONTIBUABLE                    = "numcontribuable";
    public static final String COMING_FROM_NOTIFICATION          = "coming_from_notification";
    public static final String SMS                               = "sms";
    public static final String TICKET_IMAGE_FOLDER               = "ticket_images";
    public static final int SERVICE_ACCESS_CLIENT_ID             =  2;
    public static final String SMS_ID                            = "sms_id";
    public static final String MTNCAMEROON                       = "mtncameroon";
    public static final String ORANGECAMEROON                    = "orangecameroon";
    public static final String FRENCHVERSION                     = "frenchversion";
    public static final String ENGLISHVERSION                    = "englishversion";
    public static final int READ_REQUEST_CODE                    = 200;

    public static JSONArray SEARCH_RESULT_RESPONSE;


    public static final String  SERVICE_ACCESS_CLIENT_SECRET     =  "OVywGpw9AYBDMckYSnmmLUvggMSVJGLiUdDr3Dsj";
    public static final String SERVICE_ACCESS_ACCESS_TOKEN_END_POINT     = "https://mobilebiller.idea-cm.club:4445/oauth/token";



    public static final String SCOPE_MANAGE_IDENTITIES_AND_ACCESSES = "SCOPE_MANAGE_IDENTITIES_AND_ACCESSES";
    public static boolean isParent = false;




    //public static final String ACCESS_TOKEN_EXPIRY_DATE          = "ACCESS_TOKEN_EXPIRY_DATE";




    /////////////////////////////////////////
    ///
    ///     TEST LINK
    ///
    ///////////////////////////////////////////



    //3- Paiement reussi de FOWA JOSEPH LYCEE TECHNIQUE DE NYLON-1er cycle Date 06/09/2018 12:11:22 Montant 10000 XAF Pay ID 12623. Votre Pay ID est votre preuve de paiement.
    //7- Paiement reussi de TAFOUNG CABREL LYCEE TECHNIQUE de NYLON - 2nd cycle Date 06/09/2018 11:24:47 Montant 15000 XAF Pay ID 12367. Votre Pay ID est votre preuve de paiement.
    //public static String patternsKey[] = {};


    public static String makeDateDate(long when){
        Date date = new Date(when);
        return (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)).format(date);
    }

    public static String makeUrlUserTenants(String username){
        return ROOT_URL_USER_TENANTS + username;
    }

    public static JSONArray keysPatterns(){
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObjectPayemenFraisScolarite = new JSONObject();
            jsonObjectPayemenFraisScolarite.put(Utils.KEYWORDPATTERN, "(1er\\s*cycle)|(2nd\\s*cycle)");   //OK
            jsonObjectPayemenFraisScolarite.put(Utils.TRANSACTIONTYPE, "Paiement de Frais de Scolarite");
            jsonObjectPayemenFraisScolarite.put(Utils.REGULAREXPRESSION, "Paiement\\s+reussi\\s+de\\s+" +
                    "(\\w+)((\\s+\\w+)*)\\s+(LYCEE|CES|CETIC|CETIF)\\s+(\\w+\\s+)*(DE|DU|D')" +
                    "\\s*(\\w+)\\s*(((\\s+|\\-|\\.)\\w+)*)\\s*\\-\\s*\\d(er|nd)\\s+cycle\\s+Date\\s+" +
                    "(\\d{2}\\/\\d{2}\\/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2})\\s+Montant\\s+(\\d+)\\s*(\\w+)" +
                    "\\s+Pay\\sID\\s+(\\w+)\\.\\s+Votre\\s+Pay\\s+ID\\s+est\\s+votre\\s+preuve\\s+de\\s+paiement\\.");
            jsonArray.put(jsonObjectPayemenFraisScolarite);


            JSONObject jsonObjectPayemenFraisExamen = new JSONObject();
            jsonObjectPayemenFraisExamen.put(Utils.KEYWORDPATTERN, "(PROBATOIRE|BACCALAUREAT|BEPC|CAP|PROBATOIRE\\s*TECHNIQUE|BACCALAUREAT\\s*TECHNIQUE)");
            jsonObjectPayemenFraisExamen.put(Utils.TRANSACTIONTYPE, "Paiement de Frais d'examen");  // OK
            jsonObjectPayemenFraisExamen.put(Utils.REGULAREXPRESSION, "Paiement\\s+reussi\\s+de\\s+(\\w+)((\\s+\\w+)*)\\s+" +
                    "(LYCEE|CES|CETIC|CETIF)\\s+(\\w+\\s+)*(DE|DU|D')\\s*(\\w+)\\s*(((\\s+|\\-|\\.)\\w+)*)\\s*\\-\\s*(\\w+)((\\s*\\w+)*)\\s+" +
                    "Date\\s+(\\d{2}\\/\\d{2}\\/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2})\\s+Montant\\s+(\\d+)\\s*(\\w+)\\s+Pay\\sID\\s+(\\w+)\\." +
                    "\\s+Votre\\s+Pay\\s+ID\\s+est\\s+votre\\s+preuve\\s+de\\s+paiement\\.");
            jsonArray.put(jsonObjectPayemenFraisExamen);


            JSONObject jsonObjectRetrait = new JSONObject();
            jsonObjectRetrait.put(Utils.KEYWORDPATTERN, "retrait");
            jsonObjectRetrait.put(Utils.TRANSACTIONTYPE, "Retrait"); //OK
            jsonObjectRetrait.put(Utils.REGULAREXPRESSION, "Le\\s+(\\w+)\\s+de\\s+(\\d+)\\s*(\\w+)\\s+" +
                    "initie\\s+pour\\s+(\\w+)((\\s+\\w+)*)\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})" +
                    "\\s+a\\s+ete\\s+approuve\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\.\\s*Nouveau\\s+solde:\\s+(\\d+)\\s*(\\w+)");
            jsonArray.put(jsonObjectRetrait);

            JSONObject jsonObjectPaiement = new JSONObject();
            jsonObjectPaiement.put(Utils.KEYWORDPATTERN, "Votre\\s+paiement\\s+de\\s+\\d+\\s*\\w+");
            jsonObjectPaiement.put(Utils.TRANSACTIONTYPE, "Paiement");//OK
            jsonObjectPaiement.put(Utils.REGULAREXPRESSION, "Votre\\s+paiement\\s+de\\s+(\\d+)\\s*(\\w+)\\s+pour\\s+(\\w+)((\\s+\\w+)*)\\s+a\\s+ete\\s+effectue\\s+" +
                    "le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\s*\\.\\s+Votre\\s+nouveau\\s+solde\\s*:\\s+(\\d+)\\s*(\\w+)\\s*\\." +
                    "\\s+Frais\\s*:\\s+(\\d+)\\s*(\\w+)\\s*\\,\\s+Message\\s*:\\s+(.+)\\.\\s+Transaction\\s+Id\\s*:\\s+(\\d+)\\.");
            jsonArray.put(jsonObjectPaiement);


            JSONObject jsonObjectRecharge = new JSONObject();
            jsonObjectRecharge.put(Utils.KEYWORDPATTERN, "Recharge\\s+de\\s+\\d+\\s*\\w+");  //OK
            jsonObjectRecharge.put(Utils.TRANSACTIONTYPE, "Recharge de credit");
            jsonObjectRecharge.put(Utils.REGULAREXPRESSION, "Recharge\\s+de\\s+(\\d+)\\s*(\\w+)\\s+a\\s+(\\d+)\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\s+" +
                    "effectue\\s+avec\\s+(\\w+)\\.\\s*\\w+\\s*:\\s*(\\d+)\\s*(\\w+)\\s*\\.\\s*Nouveau\\s+solde\\s*:\\s*((\\d+)(\\,\\d+)*)\\s*(\\w+)");
            jsonArray.put(jsonObjectRecharge);



            JSONObject jsonObjectEnvoi = new JSONObject();
            jsonObjectEnvoi.put(Utils.KEYWORDPATTERN, "Vous\\s+avez\\s+envoye\\s+\\d+\\s+\\w+\\s+a");  //OK
            jsonObjectEnvoi.put(Utils.TRANSACTIONTYPE, "Envoi de fond");
            jsonObjectEnvoi.put(Utils.REGULAREXPRESSION, "Vous\\s+avez\\s+envoye\\s+(\\d+)\\s+(\\w+)\\s+a\\s+" +
                    "(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4}\\-\\d{2}\\-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2})\\." +
                    "\\s*Message\\s+de\\s+l'expediteur\\s*:(.+)\\.\\s+Votre\\s+nouveau\\s+solde\\s+est\\s+de\\s*:\\s+(\\d+)\\s*(\\w+)\\.\\s+Transaction\\s+Id\\s*:\\s*(\\w+)\\.");
            jsonArray.put(jsonObjectEnvoi);

            JSONObject jsonObjectTransfert = new JSONObject();
            jsonObjectTransfert.put(Utils.KEYWORDPATTERN, "Transfert\\s+de\\s+\\d+\\s+\\w+\\s+effectue");
            jsonObjectTransfert.put(Utils.TRANSACTIONTYPE, "Transfert de fond");
            jsonObjectTransfert.put(Utils.REGULAREXPRESSION, "(\\w+)\\s+de\\s+(\\d+)\\s+(\\w+)\\s+effectue\\s+avec\\s+(\\w+)" +
                    "\\s+a\\s+(\\w+)((\\s+\\w+)*)\\s+\\((\\d+)\\)\\s+le\\s+(\\d{4})\\-(\\d{2})\\-(\\d{2})\\s+(\\d{2}):(\\d{2}):(\\d{2})\\." +
                    "\\s+FRAIS\\s+(\\d+)\\s+\\w+\\.\\s+Transaction\\s+Id:\\s+(\\d+)\\s+(;|,)\\s+Reference:(.+)\\.\\s+Nouveau\\s+solde\\s+est:\\s+(\\d+)\\s(\\w+)\\.");
            jsonArray.put(jsonObjectTransfert);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonArray;
    }

    public static String makeUrlLogin(String username){
        return ROOT_URL_LOGIN + username + "/login";
    }
    public static String makeTenantLogoUrl(String tenantid) {
        return HOST_IDENTITY_AND_ACCESS + "api/tenants/" + tenantid + "/logo";
    }
}


/*
[{"id":11,"serviceid":"61e857c0-cecd-11e8-bdce-49267d475577","clientid":"e40394e0-d0a3-11e8-a37e-6b2692fae9e0",
"tenantid":"57261bf0-d0a1-11e8-bd1f-47fec029a2ff","startdate":1539863364,"enddate":1547639364,"enablementstatus":1,"expirationstatus":0,
"reasonenablementchanged":null,"created_at":"2018-10-18 11:49:24","updated_at":"2018-10-18 11:51:30"},
{"id":12,"serviceid":"f4adac70-ce2b-11e8-99bc-1b0e3dd244de","clientid":"e40394e0-d0a3-11e8-a37e-6b2692fae9e0",
"tenantid":"57261bf0-d0a1-11e8-bd1f-47fec029a2ff","startdate":1539863646,"enddate":1547639646,"enablementstatus":1,"expirationstatus":0,
"reasonenablementchanged":null,"created_at":"2018-10-18 11:54:06","updated_at":"2018-10-18 11:54:06"}]}
 */