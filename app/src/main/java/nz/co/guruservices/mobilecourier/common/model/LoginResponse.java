package nz.co.guruservices.mobilecourier.common.model;

public class LoginResponse
        extends Response {

    private LoginResult result;

    public LoginResult getResult() {
	return result;
    }

    public void setResult(LoginResult result) {
	this.result = result;
    }

}
