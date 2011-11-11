package me.ericmiles.mobiletrans.operations;


import org.springframework.http.HttpMethod;

import android.os.Parcel;
import android.os.Parcelable;

public interface Operation {
	public abstract class OperationRequest implements Parcelable {

		public OperationRequest() {
		}

		public OperationRequest(Parcel in) {
			// nothingn right now
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// nothing right now
		}

		abstract public Class<? extends OperationResponse> getResponseType();

		/**
		 * Cheating a little bit here and exposing the implementation a
		 * tad...but I'd rather do this than create a mapping betweeen our
		 * HttpMethod and the resulting implementation's HttpMethod
		 * 
		 * @return
		 */
		abstract public HttpMethod getHttpMethod();

		abstract public String getUrl(UrlFactory factory);

	}

	public abstract class AuthenticatedOperationRequest extends OperationRequest implements Parcelable {

		public String sessionId;

		public AuthenticatedOperationRequest() {
			super();
		}

		public AuthenticatedOperationRequest(Parcel in) {
			super(in);
			sessionId = in.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(sessionId);
		}

	}

	public abstract class OperationResponse implements Parcelable {

		public enum Status {
			SUCCESS, FAILED
		};

		public Status status;
		public String errorCode;
		public String errorMsg;

		public OperationResponse() {
		}

		public OperationResponse(Parcel in) {
			status = Status.valueOf(in.readString());
			errorCode = in.readString();
			errorMsg = in.readString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#describeContents()
		 */
		@Override
		public int describeContents() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
		 */
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(status.name());
			dest.writeString(errorCode);
			dest.writeString(errorMsg);
		}

		@Override
		public String toString() {
			return "OperationResponse [status=" + status + ", errorCode=" + errorCode + ", errorMsg=" + errorMsg + "]";
		}
	}
}
