package com.rockstargames.hal;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class andColourPicker extends andView {
    static int staticCount = 0;
    private String FittedString;
    private String LockedString;
    private String NoneString;
    private int colourIndex;
    private List<SelectorGroup> dataSource;
    private boolean isMultiplayer;
    private andColourPickerImpl picker;
    private float priceMod;
    private int selectedPlatform;

    public native void onChildClick(int i, int i2, int i3, int i4, int i5, int i6);

    public native void onTryLocked(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public class SelectorItem {
        public int Colour;
        public int Cost;
        public int CostMP;
        public boolean Fitted;
        public int Index;
        public boolean Locked;
        public String Name;
        public boolean Selected;

        private SelectorItem() {
            this.Locked = false;
            this.Selected = false;
            this.Fitted = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public class SelectorGroup extends ArrayList<SelectorItem> {
        public String DisplayName;
        public String GroupName;
        public boolean Locked;

        private SelectorGroup() {
            this.Locked = false;
        }
    }

    public andColourPicker(int handle) {
        super(handle);
        this.selectedPlatform = -1;
        this.isMultiplayer = false;
        this.priceMod = 1.0f;
        this.dataSource = new ArrayList();
        this.colourIndex = 0;
        this.picker = null;
        this.LockedString = "LOCKED";
        this.FittedString = "FITTED";
        this.NoneString = "NONE";
        this.picker = new andColourPickerImpl(this, this.dataSource);
        expandableListAdapter adapter = new expandableListAdapter(ActivityWrapper.getActivity(), this.dataSource);
        this.picker.setAdapter(adapter);
        this.picker.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // from class: com.rockstargames.hal.andColourPicker.1
            @Override // android.widget.ExpandableListView.OnGroupClickListener
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        this.picker.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.rockstargames.hal.andColourPicker.2
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ((andColourPickerImpl) parent).onChildClick(groupPosition, childPosition);
                return false;
            }
        });
        setView(this.picker);
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andColourPicker createView(int handle) {
        return new andColourPicker(handle);
    }

    public void SetLocalisedStrings(String ls, String fs, String ns) {
        this.LockedString = ls;
        this.FittedString = fs;
        this.NoneString = ns;
    }

    public void AddGroup(String name, String displayString) {
        SelectorGroup newGroup = new SelectorGroup();
        newGroup.GroupName = name;
        newGroup.DisplayName = displayString;
        this.dataSource.add(newGroup);
    }

    public void AddItem(String groupName, String itemName, int cost, int costMP, int r, int g, int b, int a) {
        for (int i = 0; i < this.dataSource.size(); i++) {
            SelectorGroup group = this.dataSource.get(i);
            if (group.GroupName.equals(groupName)) {
                SelectorItem item = new SelectorItem();
                item.Name = itemName;
                item.Cost = cost;
                item.CostMP = costMP;
                item.Colour = Color.argb(a, r, g, b);
                int i2 = this.colourIndex;
                this.colourIndex = i2 + 1;
                item.Index = i2;
                group.add(item);
                return;
            }
        }
    }

    public void SetFittedItem(int groupIndex, int colourIndex) {
        this.picker.SetFittedItem(groupIndex, colourIndex);
    }

    public void SetSelectedItem(int groupIndex, int colourIndex, boolean isMP, int platform, float priceMultiplier) {
        this.picker.SetSelectedItem(groupIndex, colourIndex);
        this.isMultiplayer = isMP;
        this.selectedPlatform = platform;
        this.priceMod = priceMultiplier;
    }

    public void ResetAllToUnLocked() {
        for (int i = 0; i < this.dataSource.size(); i++) {
            SelectorGroup group = this.dataSource.get(i);
            group.Locked = false;
            for (int j = 0; j < group.size(); j++) {
                SelectorItem item = group.get(j);
                item.Locked = false;
            }
        }
    }

    public void SetItemToLocked(int itemIndex) {
        int g = 0;
        int i = itemIndex;
        SelectorGroup group = this.dataSource.get(0);
        while (true) {
            SelectorGroup group2 = group;
            if (i >= group2.size()) {
                i -= group2.size();
                g++;
                group = this.dataSource.get(g);
            } else {
                SelectorItem item = group2.get(i);
                item.Locked = true;
                return;
            }
        }
    }

    protected class andColourPickerImpl extends ExpandableListView {
        private List<SelectorGroup> dataSource;
        private andColourPicker parentPicker;
        private int previousFittedColour;
        private int previousFittedGroup;
        private int previousSelectedColour;
        private int previousSelectedGroup;

        public andColourPickerImpl(andColourPicker parent, List<SelectorGroup> source) {
            super(ActivityWrapper.getActivity());
            this.parentPicker = null;
            this.dataSource = new ArrayList();
            this.previousSelectedGroup = -1;
            this.previousSelectedColour = -1;
            this.previousFittedGroup = -1;
            this.previousFittedColour = -1;
            this.parentPicker = parent;
            this.dataSource = source;
            setChoiceMode(1);
            setGroupIndicator(null);
            setSoundEffectsEnabled(false);
        }

        public void onChildClick(int groupPosition, int childPosition) {
            try {
                SelectorItem item = this.dataSource.get(groupPosition).get(childPosition);
                if (item.Locked) {
                    this.parentPicker.onTryLocked(this.parentPicker.handle);
                } else {
                    int r = Color.red(item.Colour);
                    int g = Color.green(item.Colour);
                    int b = Color.blue(item.Colour);
                    int a = Color.alpha(item.Colour);
                    this.parentPicker.onChildClick(this.parentPicker.handle, r, g, b, a, item.Index);
                }
            } catch (Exception e) {
                ActivityWrapper.logError("andColourPicker", "Exception in onChildClick " + groupPosition + " " + childPosition + "\n" + ReportPickerStatus(), e);
            }
        }

        public void SetSelectedItem(int groupIndex, int colourIndex) {
            try {
                if (this.previousSelectedColour >= 0) {
                    SelectorGroup prevGroup = this.dataSource.get(this.previousSelectedGroup);
                    SelectorItem prevItem = prevGroup.get(this.previousSelectedColour);
                    prevItem.Selected = false;
                }
            } catch (Exception e) {
                ActivityWrapper.logError("andColourPicker", "Exception resetting selected item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e);
            }
            try {
                SelectorGroup group = this.dataSource.get(groupIndex);
                SelectorItem item = group.get(colourIndex);
                item.Selected = true;
                this.previousSelectedGroup = groupIndex;
                this.previousSelectedColour = colourIndex;
                invalidateViews();
            } catch (Exception e2) {
                ActivityWrapper.logError("andColourPicker", "Exception setting selected item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e2);
            }
        }

        public void SetFittedItem(int groupIndex, int colourIndex) {
            try {
                if (this.previousFittedColour >= 0) {
                    SelectorGroup prevGroup = this.dataSource.get(this.previousFittedGroup);
                    SelectorItem prevItem = prevGroup.get(this.previousFittedColour);
                    prevItem.Fitted = false;
                }
            } catch (Exception e) {
                ActivityWrapper.logError("andColourPicker", "Exception resetting fitted item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e);
            }
            try {
                SelectorGroup group = this.dataSource.get(groupIndex);
                SelectorItem item = group.get(colourIndex);
                item.Fitted = true;
                this.previousFittedGroup = groupIndex;
                this.previousFittedColour = colourIndex;
            } catch (Exception e2) {
                ActivityWrapper.logError("andColourPicker", "Exception setting fitted item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e2);
            }
        }

        private String ReportPickerStatus() {
            String returnString = "ColourPicker Status - \n".concat("dataSource.size() == " + this.dataSource.size() + "\n");
            for (int i = 0; i < this.dataSource.size(); i++) {
                SelectorGroup group = this.dataSource.get(i);
                returnString = returnString.concat("group " + i + " size() == " + group.size() + "\n");
            }
            return returnString;
        }
    }

    protected class expandableListAdapter extends BaseExpandableListAdapter {
        private Activity context;
        private List<SelectorGroup> dataSource;

        public expandableListAdapter(Activity c, List<SelectorGroup> source) {
            this.context = null;
            this.dataSource = null;
            this.context = c;
            this.dataSource = source;
        }

        @Override // android.widget.ExpandableListAdapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.ExpandableListAdapter
        public Object getGroup(int position) {
            return this.dataSource.get(position);
        }

        @Override // android.widget.ExpandableListAdapter
        public Object getChild(int groupPosition, int childPosition) {
            return this.dataSource.get(groupPosition).get(childPosition);
        }

        @Override // android.widget.ExpandableListAdapter
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override // android.widget.ExpandableListAdapter
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override // android.widget.ExpandableListAdapter
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) { return null; }

        @Override // android.widget.ExpandableListAdapter
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) { return null; }

        @Override // android.widget.ExpandableListAdapter
        public int getGroupCount() {
            return this.dataSource.size();
        }

        @Override // android.widget.ExpandableListAdapter
        public int getChildrenCount(int groupPosition) {
            return this.dataSource.get(groupPosition).size();
        }

        @Override // android.widget.ExpandableListAdapter
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}