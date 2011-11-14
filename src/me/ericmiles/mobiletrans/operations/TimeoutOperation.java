package me.ericmiles.mobiletrans.operations;


import me.ericmiles.mobiletrans.R;

import org.springframework.http.HttpMethod;

import android.os.Parcel;
import android.os.Parcelable;

public interface TimeoutOperation extends Operation {

	public static class Request extends AuthenticatedOperationRequest implements Parcelable {

		public boolean retry;

		public Request() {
		}

		public Request(Parcel in) {
			super(in);
			retry = Boolean.parseBoolean(in.readString());
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(Boolean.toString(retry));
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
			return R.string.url_timeout_operation;
		}

		@Override
		public String toString() {
			return "Request [retry=" + retry + ", toString()=" + super.toString() + "]";
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

		public Response() {
		}

		public Response(Parcel in) {
			super(in);
		}

		@Override
		public int describeContents() {
			return super.describeContents();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
		}

		@Override
		public String toString() {
			return super.toString();
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
