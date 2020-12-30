package core.Widgets;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class CustomTextRefresh extends AppCompatTextView {
    public CustomTextRefresh(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextRefresh(Context context) {
        super(context);
        init();
    }


    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/spiritmedium.ttf");
        setTypeface(tf);

    }
}
