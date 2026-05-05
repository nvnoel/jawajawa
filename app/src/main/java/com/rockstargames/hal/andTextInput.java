package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import java.util.ArrayList;

public class andTextInput extends andLabel {
    static int staticCount = 0;
    Bitmap bitmap;
    private boolean clearOnFocus;
    private ArrayList<InputFilter> filters;
    private boolean isPasswordInput;
    boolean setProgrammatically;

    enum TextAlignment {
        TEXT_ALIGNMENT_CENTER,
        TEXT_ALIGNMENT_LEFT,
        TEXT_ALIGNMENT_RIGHT,
        TEXT_ALIGNMENT_JUSTIFY
    }

    public native void textChanged(int i, String str);

    public andTextInput(int handle) {
        super(handle, false);
        this.setProgrammatically = false;
        this.isPasswordInput = false;
        this.clearOnFocus = false;
        this.filters = new ArrayList<>();
        setView(new andTextInputImpl());
        getView().setFocusable(true);
        staticCount++;
    }

    public void setNextFocusView(andView view) {
        int nextFocusID = view.getView().getId();
        getView().setNextFocusLeftId(nextFocusID);
        getView().setNextFocusUpId(nextFocusID);
        getView().setNextFocusDownId(nextFocusID);
        getView().setNextFocusRightId(nextFocusID);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    @Override // com.rockstargames.hal.andLabel
    public void setText(String text) {
        this.setProgrammatically = true;
        super.setText(text);
    }

    @Override // com.rockstargames.hal.andLabel
    public void setTextSize(float points) {
        super.setTextSize(points);
        ((andTextInputImpl) this.view).setSingleLine(true);
        if (this.isPasswordInput) {
            ((andTextInputImpl) this.view).setInputType(129);
            ((andTextInputImpl) this.view).setTypeface(Typeface.DEFAULT);
        }
    }

    @Override // com.rockstargames.hal.andLabel
    public void setBackgroundTransparent() {
        ((andTextInputImpl) this.view).setBackgroundColor(0);
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        super.setBounds(x, y, w, h, 0.0f, 0.0f);
    }

    public static andTextInput createView(int handle) {
        return new andTextInput(handle);
    }

    public void setEmail(boolean isEmail) {
        this.setProgrammatically = true;
        ((andTextInputImpl) this.view).setInputType(524321);
    }

    public void setPassword(boolean isPassword) {
        this.setProgrammatically = true;
        this.isPasswordInput = true;
        ((andTextInputImpl) this.view).setInputType(129);
        ((andTextInputImpl) this.view).setTypeface(Typeface.DEFAULT);
    }

    public void setNumberInput() {
        ((andTextInputImpl) this.view).setInputType(2);
    }

    public void setLicensePlateInput() {
        InputFilter charFilter = new InputFilter() { // from class: com.rockstargames.hal.andTextInput.1
            @Override // android.text.InputFilter
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if ((source.charAt(i) < 'a' || source.charAt(i) > 'z') && ((source.charAt(i) < 'A' || source.charAt(i) > 'Z') && (source.charAt(i) < '0' || source.charAt(i) > '9'))) {
                        return "";
                    }
                }
                return null;
            }
        };
        InputFilter lengthFilter = new InputFilter.LengthFilter(8);
        this.filters.add(charFilter);
        this.filters.add(lengthFilter);
        ((andTextInputImpl) this.view).updateFilters();
        ((andTextInputImpl) this.view).setInputType(528385);
    }

    public void setWatermarkText(String hint) {
        ((andTextInputImpl) this.view).setHint(hint);
    }

    public void setBackgroundImage(andImage img) {
        andTextInputImpl txt = (andTextInputImpl) this.view;

    }

    public void setMaxLength(int length) {
        andTextInputImpl txt = (andTextInputImpl) this.view;
        txt.setMaxLength(length);
    }

    public void setClearOnFocus(boolean b) {
        this.clearOnFocus = b;
    }

    @Override // com.rockstargames.hal.andLabel
    public void setTextAlignment(int alignment) {
        switch (TextAlignment.values()[alignment]) {
            case TEXT_ALIGNMENT_CENTER:
                ((andTextInputImpl) this.view).setGravity(1);
                return;
            case TEXT_ALIGNMENT_LEFT:
                ((andTextInputImpl) this.view).setGravity(3);
                return;
            case TEXT_ALIGNMENT_RIGHT:
                ((andTextInputImpl) this.view).setGravity(5);
                return;
            case TEXT_ALIGNMENT_JUSTIFY:
                Log.e("andTextInput", "WARNING: Justified alignment not implemented yet.");
                return;
            default:
                return;
        }
    }

    protected class andTextInputImpl extends EditText {
        boolean attached;
        Object packedImage;

        public andTextInputImpl() {
            super(ActivityWrapper.getActivity());
            this.packedImage = null;
            this.attached = false;
            setId(andViewManager.genID());
            addTextChangedListener(new TextWatcher() { // from class: com.rockstargames.hal.andTextInput.andTextInputImpl.1
                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (andTextInput.this.setProgrammatically) {
                        andTextInput.this.setProgrammatically = false;
                    } else {
                        andTextInput.this.textChanged(andTextInput.this.handle, s.toString());
                    }
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                }
            });
        }

        @Override // android.widget.TextView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
        }

        public boolean isAttached() {
            return this.attached;
        }

        public void setPackedImage(Object packedImage) {
            this.packedImage = packedImage;
        }

        @Override // android.widget.TextView, android.view.View
        public void onDraw(Canvas canvas) {
            if (this.packedImage != null) {

            }
            super.onDraw(canvas);
        }

        @Override // android.widget.TextView, android.view.View
        public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            if (focused && andTextInput.this.clearOnFocus) {
                setText("");
            }
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }

        public void setMaxLength(int length) {
            andTextInput.this.filters.add(new InputFilter.LengthFilter(length));
            updateFilters();
        }

        public void updateFilters() {
            if (andTextInput.this.filters.size() > 0) {
                setFilters((InputFilter[]) andTextInput.this.filters.toArray(new InputFilter[andTextInput.this.filters.size()]));
            }
        }
    }
}