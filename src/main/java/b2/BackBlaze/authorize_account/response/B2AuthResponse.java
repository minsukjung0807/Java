package b2.BackBlaze.authorize_account.response;

/**
 * b2_authorize_account으로 전달 받은 결과 값을 나타내는 클래스입니다.
 */
public class B2AuthResponse {

    private String authorizationToken, accountID, apiUrl, downloadURL;

    /**
     * B2 API로부터 전달 받은 값들을 사용하여 B2AuthResponse를 생성합니다.
     * 
     * @param authorizationToken : B2로부터 반환된 인증 토큰입니다
     * @param accountID : B2로부터 반환된 accountID입니다
     * @param apiUrl : B2의 API 호출을 위해서 사용되는 URL입니다
     * @param downloadURL : 파일을 다운로드 받기위해 사용되는 URL입니다
     */
    public B2AuthResponse(String authorizationToken, String accountID, String apiUrl, String downloadURL){
        this.authorizationToken = authorizationToken;
        this.accountID = accountID;
        this.apiUrl = apiUrl;
        this.downloadURL = downloadURL;
    }

    /**
     * 현재 세션의 인증 토큰을 반환합니다.
     *
     * @return Authorization used for the HTTP Authorization header in future requests
     */
    public String getAuthToken(){
        return authorizationToken;
    }

    /**
     * Returns the account ID which owns this session.
     *
     * @return Account ID representing the owner of the session
     */
    public String getAccountID(){
        return accountID;
    }

    /**
     * Returns the APIURL which should be used for further requests.
     *
     * @return APIURL which should be used for further requests
     */
    public String getAPIURL(){
        return apiUrl;
    }

    /**
     * Returns the downloadURL for this session.
     *
     * @return downloadURL which should be used when retrieving files
     */
    public String getDownloadURL(){
        return downloadURL;
    }

}
