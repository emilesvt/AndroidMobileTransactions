/**
 * 
 */
package me.ericmiles.mobiletrans.operations;


import org.springframework.http.HttpMethod;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author 94728
 * 
 */
public interface LoginOperation extends Operation {

	public static class Request extends OperationRequest implements Parcelable {
		public String userId;
		public String password;

		public Request() {
		}

		public Request(Parcel in) {
			super(in);
			userId = in.readString();
			password = in.readString();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(userId);
			dest.writeString(password);
		}

		@Override
		public String toString() {
			return "Request [userId=" + userId + ", password=" + password + ", toString()=" + super.toString() + "]";
		}

		@Override
		public Class<? extends OperationResponse> getResponseType() {
			return Response.class;
		}

		@Override
		public HttpMethod getHttpMethod() {
			return HttpMethod.POST;
		}

		@Override
		public String getUrl(UrlFactory factory) {
			return factory.getUrl(this);
		}

		public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
			@Override
			public Request createFromParcel(Parcel source) {
				return new Request(source);
			}

			@Override
			public Request[] newArray(int size) {
				return new Request[size];
			}
		};
	}

	public static class Response extends OperationResponse implements Parcelable {

		public String sessionId;

		public Response(Parcel in) {
			super(in);
			sessionId = in.readString();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(sessionId);
		}

		@Override
		public String toString() {
			return "Response [sessionId=" + sessionId + ", toString()=" + super.toString() + "]";
		}

		public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
			@Override
			public Response createFromParcel(Parcel source) {
				return new Response(source);
			}

			@Override
			public Response[] newArray(int size) {
				return new Response[size];
			}
		};
	}

}
