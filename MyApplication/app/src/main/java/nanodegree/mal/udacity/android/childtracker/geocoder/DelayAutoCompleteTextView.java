package nanodegree.mal.udacity.android.childtracker.geocoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by MOSTAFA on 12/11/2016.
 */

//if we use the normal AutoCompleteTextView the filter function fires on every keypress.
// If you did this with an address on the Geocoder API the requests will be throttled and no results will be returned.
//throttled when large numbers of requests are sending to google server so it prevent this large numbers of requests

public class DelayAutoCompleteTextView extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;

    private int autoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar loadingIndicator;

    //handler to send request after some delay time
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DelayAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        loadingIndicator = progressBar;
    }

    public void setAutoCompleteDelay(int autoCompleteDelay) {
        this.autoCompleteDelay = autoCompleteDelay;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
        handler.removeMessages(MESSAGE_TEXT_CHANGED);
        handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, text), autoCompleteDelay);

    }

    @Override
    public void onFilterComplete(int count) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }
}
