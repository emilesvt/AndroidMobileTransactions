/**
 * 
 */
package me.ericmiles.mobiletrans.operations;


import org.springframework.http.HttpMethod;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author emiles
 * 
 */
public interface LogoutOperation extends Operation {

	public static class Request extends AuthenticatedOperationRequest implements Parcelable {
		
		public Request(Parcel source) {
			super(source);
		}
		
		public Request() {
		}

		@Override
		public Class<? extends OperationResponse> getResponseType() {
			return Response.class;
		}

		@Override
		public HttpMethod getHttpMethod() {
			return HttpMethod.GET;
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
		public enum Status {
			SUCCESS, FAILED
		};

		public Status status;

		public Response(Parcel in) {
			super(in);
			status = Status.valueOf(in.readString());
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(status.name());
		}

		@Override
		public String toString() {
			return "Response [status=" + status + ", toString()=" + super.toString() + "]";
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
