package io.eberlein.smthnspcl.drinkteawithme;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

class API {
    private TeaService service;

    API() {
        service = createService();
    }

    private static TeaService createService() {
        Retrofit client = new Retrofit.Builder().baseUrl("http://192.168.2.117:7344/").addConverterFactory(GsonConverterFactory.create()).build();
        return client.create(TeaService.class);
    }

    private void doEnqueuedCall_Success(Call<SuccessResponse> call, onSomething onSuccess, onSomething onFailure) {
        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().success) onSuccess.execute();
                    else onFailure.execute();
                } else {
                    onFailure.execute();
                }
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {
                onFailure.execute(); // todo own onSomething class
            }
        });
    }

    private void doEnqueuedCall_String(Call<StringResponse> call, onSomething onSuccess, onSomething onFailure) {
        call.enqueue(new Callback<StringResponse>() {
            @Override
            public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().value != null) onSuccess.execute(response.body().value);
                    else onFailure.execute();
                } else {
                    onFailure.execute();
                }
            }

            @Override
            public void onFailure(Call<StringResponse> call, Throwable t) {
                onFailure.execute(); // todo own onSomething class
            }
        });
    }

    void createUser(User data, onSomething onSuccess, onSomething onFailure) {
        this.doEnqueuedCall_Success(service.createUser(data), onSuccess, onFailure);
    }

    void loginUser(User data, onSomething onSuccess, onSomething onFailure) {
        this.doEnqueuedCall_Success(service.loginUser(data), onSuccess, onFailure);
    }

    void inviteUser(User data, onSomething onSuccess, onSomething onFailure) {
        this.doEnqueuedCall_String(service.inviteUser(data), onSuccess, onFailure);
    }

    public interface TeaService {
        @POST("api/user/create")
        Call<SuccessResponse> createUser(@Body User data);

        @POST("api/user/login")
        Call<SuccessResponse> loginUser(@Body User data);

        @POST("api/user/invite")
        Call<StringResponse> inviteUser(@Body User data);
    }

    public interface onSomething {
        void execute();

        void execute(String data);
    }

    public static class on implements onSomething {
        @Override
        public void execute() {

        }

        @Override
        public void execute(String data) {

        }
    }

    class IResponse {
        String key;
    }

    class SuccessResponse extends IResponse {
        Boolean success;
    }

    class StringResponse extends IResponse {
        String value;
    }
}
