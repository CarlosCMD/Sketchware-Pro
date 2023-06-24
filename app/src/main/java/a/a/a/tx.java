package a.a.a;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.besome.sketch.beans.ProjectResourceBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.sketchware.remod.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import mod.hey.studios.util.Helper;

public class tx extends RelativeLayout implements View.OnClickListener {
    public String a;
    public String b;
    public String c;
    public boolean d;
    public TextView e;
    public TextView f;
    public ImageView g;
    public ImageView h;
    public RadioGroup i;
    public LinearLayout j;
    public View k;
    public View l;
    public int m;
    public Kw n;

    public tx(Context context, boolean z, String str, boolean z2) {
        super(context);
        this.d = false;
        this.a = str;
        a(context, z, z2);
    }

    public String getKey() {
        return this.b;
    }

    public String getValue() {
        return this.c;
    }

    @Override
    public void onClick(View view) {
        if (mB.a()) {
            return;
        }
        a();
    }

    public void setKey(String str) {
        this.b = str;
        int identifier = getResources().getIdentifier(str, "string", getContext().getPackageName());
        if (identifier > 0) {
            this.e.setText(xB.b().a(getResources(), identifier));
            if ("property_image".equals(b)) {
                m = R.drawable.ic_picture_48dp;
            } else if ("property_background_resource".equals(b)) {
                m = R.drawable.variation_48;
            }
            if (this.l.getVisibility() == VISIBLE) {
                ((ImageView) findViewById(R.id.img_icon)).setImageResource(this.m);
                ((TextView) findViewById(R.id.tv_title)).setText(xB.b().a(getContext(), identifier));
            } else {
                this.h.setImageResource(this.m);
            }
        }
    }

    public void setOnPropertyValueChangeListener(Kw kw) {
        this.n = kw;
    }

    public void setOrientationItem(int i) {
        if (i == 0) {
            this.k.setVisibility(GONE);
            this.l.setVisibility(VISIBLE);
            return;
        }
        this.k.setVisibility(VISIBLE);
        this.l.setVisibility(GONE);
    }

    public void setValue(String str) {
        Uri fromFile;
        if (str != null && !str.toLowerCase().equals("NONE".toLowerCase())) {
            this.c = str;
            this.f.setText(str);
            if (jC.d(this.a).h(str) == ProjectResourceBean.PROJECT_RES_TYPE_RESOURCE) {
                this.g.setImageResource(getContext().getResources().getIdentifier(str, "drawable", getContext().getPackageName()));
                return;
            } else if (str.equals("default_image")) {
                this.g.setImageResource(getContext().getResources().getIdentifier(str, "drawable", getContext().getPackageName()));
                return;
            } else {
                File file = new File(jC.d(this.a).f(str));
                if (file.exists()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        Context context = getContext();
                        fromFile = FileProvider.getUriForFile(context, getContext().getPackageName() + ".provider", file);
                    } else {
                        fromFile = Uri.fromFile(file);
                    }
                    Glide.with(getContext()).load(fromFile).signature((Key) kC.n()).error(R.drawable.ic_remove_grey600_24dp).into(this.g);
                    return;
                }
                this.g.setImageResource(getContext().getResources().getIdentifier(str, "drawable", getContext().getPackageName()));
                return;
            }
        }
        this.c = str;
        this.f.setText("NONE");
        this.g.setImageDrawable(null);
        this.g.setBackgroundColor(Color.WHITE);
    }

    public final void a(Context context, boolean z, boolean z2) {
        wB.a(context, this, R.layout.property_resource_item);
        this.e = (TextView) findViewById(R.id.tv_name);
        this.f = (TextView) findViewById(R.id.tv_value);
        this.g = (ImageView) findViewById(R.id.view_image);
        this.h = (ImageView) findViewById(R.id.img_left_icon);
        this.k = findViewById(R.id.property_item);
        this.l = findViewById(R.id.property_menu_item);
        this.d = z2;
        if (z) {
            setOnClickListener(this);
            setSoundEffectsEnabled(true);
        }
    }

    public final void a() {
        LinearLayout a2;
        aB aBVar = new aB((Activity) getContext());
        aBVar.b(this.e.getText().toString());
        aBVar.a(this.m);
        View a3 = wB.a(getContext(), R.layout.property_popup_selector_color);
        ScrollView scrollView = (ScrollView) a3.findViewById(R.id.scroll_view);
        this.i = (RadioGroup) a3.findViewById(R.id.rg);
        this.j = (LinearLayout) a3.findViewById(R.id.content);
        ArrayList<String> m = jC.d(this.a).m();
        if (xq.a(this.a) || xq.b(this.a)) {
            if (this.d) {
                m.add(0, "default_image");
            } else {
                m.add(0, "NONE");
            }
        }
        Iterator<String> it = m.iterator();
        RadioButton radioButton = null;
        while (it.hasNext()) {
            String next = it.next();
            RadioButton a4 = a(next);
            this.i.addView(a4);
            if (next.equals(this.c)) {
                a4.setChecked(true);
                radioButton = a4;
            }
            if (xq.a(this.a)) {
                if (next.equals("default_image")) {
                    a2 = a(next, true);
                } else {
                    a2 = a(next, false);
                }
            } else if (xq.b(this.a)) {
                if (next.equals("default_image")) {
                    a2 = a(next, true);
                } else {
                    a2 = a(next, false);
                }
            } else {
                a2 = a(next, true);
            }
            a2.setOnClickListener(v -> ((RadioButton) i.getChildAt(j.indexOfChild(v))).setChecked(true));
            this.j.addView(a2);
        }
        if (radioButton == null) {
            radioButton = (RadioButton) this.i.getChildAt(0);
            radioButton.setChecked(true);
        }
        aBVar.a(a3);
        aBVar.b(xB.b().a(getContext(), R.string.common_word_select), v -> {
            int childCount = i.getChildCount();
            int i = 0;
            while (i < childCount) {
                RadioButton child = (RadioButton) tx.this.i.getChildAt(i);
                if (child.isChecked()) {
                    setValue(child.getTag().toString());
                    if (n != null) {
                        n.a(b, c);
                    }
                } else {
                    i++;
                }
            }
            aBVar.dismiss();
        });
        aBVar.a(xB.b().a(getContext(), R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        RadioButton finalRadioButton = radioButton;
        aBVar.setOnShowListener(dialog -> scrollView.smoothScrollTo(0, (int) finalRadioButton.getY()));
        aBVar.show();
    }

    public final RadioButton a(String str) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText("");
        radioButton.setTag(str);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                (int) (wB.a(getContext(), 1.0f) * 60.0f));
        radioButton.setGravity(Gravity.CENTER | Gravity.LEFT);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    public final LinearLayout a(String str, boolean z) {
        Uri fromFile;
        float a2 = wB.a(getContext(), 1.0f);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (60.0f * a2)));
        linearLayout.setGravity(Gravity.CENTER | Gravity.LEFT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        layoutParams.rightMargin = (int) (8.0f * a2);
        textView.setLayoutParams(layoutParams);
        textView.setText(str);
        linearLayout.addView(textView);
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int i = (int) (a2 * 48.0f);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(i, i));
        if (!str.toLowerCase().equals("NONE".toLowerCase())) {
            if (z) {
                imageView.setImageResource(getContext().getResources().getIdentifier(str, "drawable", getContext().getPackageName()));
            } else {
                File file = new File(jC.d(this.a).f(str));
                if (file.exists()) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        Context context = getContext();
                        fromFile = FileProvider.getUriForFile(context, getContext().getPackageName() + ".provider", file);
                    } else {
                        fromFile = Uri.fromFile(file);
                    }
                    Glide.with(getContext()).load(fromFile).signature((Key) kC.n()).error(R.drawable.ic_remove_grey600_24dp).into(imageView);
                } else {
                    imageView.setImageResource(getContext().getResources().getIdentifier(str, "drawable", getContext().getPackageName()));
                }
            }
            imageView.setBackgroundResource(R.drawable.bg_outline);
        } else {
            imageView.setBackgroundResource(R.drawable.bg_outline);
        }
        linearLayout.addView(imageView);
        return linearLayout;
    }
}
