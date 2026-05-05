package com.rockstargames.hal;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class andTable extends andView {
    static int staticCount = 0;
    protected int count;

    /* JADX INFO: Access modifiers changed from: private */
    public native andView getCell(int i, int i2, int i3);

    public andTable(int handle) {
        super(handle);
        this.count = 0;
        setView(new andTableImpl());
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andTable createView(int handle) {
        return new andTable(handle);
    }

    protected andTableImpl getTable() {
        return (andTableImpl) this.view;
    }

    public void setCellCount(int count) {
        this.count = count;
        getTable().notifyObservers();
        getTable().invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class andTableImpl extends ListView implements ListAdapter {
        private HashMap<View, andView> nativeToHalMap;
        private HashSet<DataSetObserver> observers;

        public andTableImpl() {
            super(ActivityWrapper.getActivity());
            this.nativeToHalMap = new HashMap<>();
            this.observers = new HashSet<>();
            setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            setAdapter((ListAdapter) this);
        }

        @Override // android.widget.AdapterView, android.widget.Adapter
        @ViewDebug.CapturedViewProperty
        public int getCount() {
            return andTable.this.count;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int position) {
            return 0;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            andView recycle = null;
            if (convertView != null) {
                andView recycle2 = this.nativeToHalMap.get(convertView);
                recycle = recycle2;
            }
            andView returnedCell = andTable.this.getCell(andTable.this.handle, position, recycle == null ? 0 : recycle.getHandle());
            if (returnedCell == null) {
                Log.e("andTable", "*** *** Returned cell was null! *** ***");
            }
            if (recycle != null && returnedCell != recycle) {
                this.nativeToHalMap.remove(convertView);
            } else {
                this.nativeToHalMap.put(returnedCell.getOuterView(), returnedCell);
            }
            return returnedCell.getOuterView();
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver observer) {
            this.observers.add(observer);
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.observers.remove(observer);
        }

        public void notifyObservers() {
            Iterator it = new HashSet(this.observers).iterator();
            while (it.hasNext()) {
                DataSetObserver dso = (DataSetObserver) it.next();
                dso.onChanged();
            }
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int arg0) {
            return true;
        }
    }
}