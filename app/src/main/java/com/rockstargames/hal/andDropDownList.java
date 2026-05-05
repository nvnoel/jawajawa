package com.rockstargames.hal;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;
import java.util.List;

public class andDropDownList extends andView {
    static int staticCount = 0;
    List<listData> listElements;
    int selectedItem;

    public class listData {
        String data;
        String label;

        public listData() {
        }

        public String toString() {
            return this.label;
        }
    }

    public andDropDownList(int handle) {
        super(handle);
        this.selectedItem = -1;
        andDropDownListImpl l = new andDropDownListImpl();
        setView(l);
        this.listElements = new ArrayList();
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andDropDownList createView(int handle) {
        return new andDropDownList(handle);
    }

    public int getWidth() {
        return getContainer().getWidth();
    }

    public int getHeight() {
        return getContainer().getHeight();
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        super.setBounds(x, y, w, h, rightPadding, bottomPadding);
    }

    protected Spinner GetSpinner() {
        return (Spinner) this.view;
    }

    public void AddListItem(String label, String data) {
        listData element = new listData();
        element.label = label;
        element.data = data;
        this.listElements.add(element);
    }

    public void BuildList() {
        Spinner dropDown = GetSpinner();
        ArrayAdapter<listData> dataAdapter = new ArrayAdapter<>(ActivityWrapper.getActivity(), 17367048, this.listElements);
        dataAdapter.setDropDownViewResource(17367049);
        dropDown.setAdapter((SpinnerAdapter) dataAdapter);
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.rockstargames.hal.andDropDownList.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                andDropDownList.this.selectedItem = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public String GetSelectedItemData() {
        if (this.selectedItem != -1) {
            String data = this.listElements.get(this.selectedItem).data;
            Log.i("data", "data code: " + data);
            return data;
        }
        return null;
    }

    protected class andDropDownListImpl extends Spinner {
        public andDropDownListImpl() {
            super(ActivityWrapper.getActivity());
            setId(andViewManager.genID());
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            andDropDownList.this.onAttachedToWindow(andDropDownList.this.getHandle());
        }
    }
}