/**
 * 
 */
package me.ericmiles.mobiletrans.operations;


import me.ericmiles.mobiletrans.R;

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
			return HttpMethod.POST;
		}

		@Override
		public int getUrlResourceId() {
			return R.string.url_logout_operation;
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
		public Response(Parcel in) {
			super(in);
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
