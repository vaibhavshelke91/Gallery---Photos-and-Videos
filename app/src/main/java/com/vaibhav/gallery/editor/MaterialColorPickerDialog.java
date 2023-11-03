package com.vaibhav.gallery.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;

import com.skydoves.colorpickerview.databinding.DialogColorpickerColorpickerviewSkydovesBinding;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;
import com.skydoves.colorpickerview.preference.ColorPickerPreferenceManager;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

public class MaterialColorPickerDialog extends ColorPickerDialog {

    public MaterialColorPickerDialog(Context context) {
        super(context);
    }


    public static class Builder extends MaterialAlertDialogBuilder {
        private DialogColorpickerColorpickerviewSkydovesBinding dialogBinding;
        private ColorPickerView colorPickerView;
        private boolean shouldAttachAlphaSlideBar = true;
        private boolean shouldAttachBrightnessSlideBar = true;
        private int bottomSpace = SizeUtils.dp2Px(getContext(), 10);

        public Builder(Context context) {
            super(context);
            onCreate();
        }

        public Builder(Context context, int themeResId) {
            super(context, themeResId);
            onCreate();
        }

        private void onCreate() {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            this.dialogBinding =
                    DialogColorpickerColorpickerviewSkydovesBinding.inflate(layoutInflater, null, false);
            this.colorPickerView = dialogBinding.colorPickerView;
            this.colorPickerView.attachAlphaSlider(dialogBinding.alphaSlideBar);
            this.colorPickerView.attachBrightnessSlider(dialogBinding.brightnessSlideBar);
            this.colorPickerView.setColorListener(
                    new ColorEnvelopeListener() {
                        @Override
                        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                            // no stubs
                        }
                    });
            super.setView(dialogBinding.getRoot());
        }

        /**
         * gets {@link ColorPickerView} on {@link ColorPickerDialog.Builder}.
         *
         * @return {@link ColorPickerView}.
         */
        public ColorPickerView getColorPickerView() {
            return colorPickerView;
        }

        /**
         * sets {@link ColorPickerView} manually.
         *
         * @param colorPickerView {@link ColorPickerView}.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder setColorPickerView(ColorPickerView colorPickerView) {
            this.dialogBinding.colorPickerViewFrame.removeAllViews();
            this.dialogBinding.colorPickerViewFrame.addView(colorPickerView);
            return this;
        }

        /**
         * if true, attaches a {@link AlphaSlideBar} on the {@link ColorPickerDialog}.
         *
         * @param value true or false.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder attachAlphaSlideBar(boolean value) {
            this.shouldAttachAlphaSlideBar = value;
            return this;
        }

        /**
         * if true, attaches a {@link BrightnessSlideBar} on the {@link ColorPickerDialog}.
         *
         * @param value true or false.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder attachBrightnessSlideBar(boolean value) {
            this.shouldAttachBrightnessSlideBar = value;
            return this;
        }

        /**
         * sets the preference name.
         *
         * @param preferenceName preference name.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder setPreferenceName(String preferenceName) {
            if (getColorPickerView() != null) {
                getColorPickerView().setPreferenceName(preferenceName);
            }
            return this;
        }

        /**
         * sets the margin of the bottom. this space visible when {@link AlphaSlideBar} or {@link
         * BrightnessSlideBar} is attached.
         *
         * @param bottomSpace space of the bottom.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder setBottomSpace(int bottomSpace) {
            this.bottomSpace = SizeUtils.dp2Px(getContext(), bottomSpace);
            return this;
        }

        /**
         * sets positive button with {@link ColorPickerViewListener} on the {@link ColorPickerDialog}.
         *
         * @param textId string resource integer id.
         * @param colorListener {@link ColorListener}.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder setPositiveButton(int textId, final ColorPickerViewListener colorListener) {
            super.setPositiveButton(textId, getOnClickListener(colorListener));
            return this;
        }

        /**
         * sets positive button with {@link ColorPickerViewListener} on the {@link ColorPickerDialog}.
         *
         * @param text string text value.
         * @param colorListener {@link ColorListener}.
         * @return {@link ColorPickerDialog.Builder}.
         */
        public MaterialColorPickerDialog.Builder setPositiveButton(
                CharSequence text, final ColorPickerViewListener colorListener) {
            super.setPositiveButton(text, getOnClickListener(colorListener));
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setNegativeButton(int textId, OnClickListener listener) {
            super.setNegativeButton(textId, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            super.setNegativeButton(text, listener);
            return this;
        }

        private OnClickListener getOnClickListener(final ColorPickerViewListener colorListener) {
            return new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (colorListener instanceof ColorListener) {
                        ((ColorListener) colorListener).onColorSelected(getColorPickerView().getColor(), true);
                    } else if (colorListener instanceof ColorEnvelopeListener) {
                        ((ColorEnvelopeListener) colorListener)
                                .onColorSelected(getColorPickerView().getColorEnvelope(), true);
                    }
                    if (getColorPickerView() != null) {
                        ColorPickerPreferenceManager.getInstance(getContext())
                                .saveColorPickerData(getColorPickerView());
                    }
                }
            };
        }

        /**
         * shows a created {@link ColorPickerDialog}.
         *
         * @return {@link AlertDialog}.
         */
        @Override
        @NonNull
        public AlertDialog create() {
            if (getColorPickerView() != null) {
                this.dialogBinding.colorPickerViewFrame.removeAllViews();
                this.dialogBinding.colorPickerViewFrame.addView(getColorPickerView());

                AlphaSlideBar alphaSlideBar = getColorPickerView().getAlphaSlideBar();
                if (shouldAttachAlphaSlideBar && alphaSlideBar != null) {
                    this.dialogBinding.alphaSlideBarFrame.removeAllViews();
                    this.dialogBinding.alphaSlideBarFrame.addView(alphaSlideBar);
                    this.getColorPickerView().attachAlphaSlider(alphaSlideBar);
                } else if (!shouldAttachAlphaSlideBar) {
                    this.dialogBinding.alphaSlideBarFrame.removeAllViews();
                }

                BrightnessSlideBar brightnessSlideBar = getColorPickerView().getBrightnessSlider();
                if (shouldAttachBrightnessSlideBar && brightnessSlideBar != null) {
                    this.dialogBinding.brightnessSlideBarFrame.removeAllViews();
                    this.dialogBinding.brightnessSlideBarFrame.addView(brightnessSlideBar);
                    this.getColorPickerView().attachBrightnessSlider(brightnessSlideBar);
                } else if (!shouldAttachBrightnessSlideBar) {
                    this.dialogBinding.brightnessSlideBarFrame.removeAllViews();
                }

                if (!shouldAttachAlphaSlideBar && !shouldAttachBrightnessSlideBar) {
                    this.dialogBinding.spaceBottom.setVisibility(View.GONE);
                } else {
                    this.dialogBinding.spaceBottom.setVisibility(View.VISIBLE);
                    this.dialogBinding.spaceBottom.getLayoutParams().height = bottomSpace;
                }
            }

            super.setView(dialogBinding.getRoot());
            return super.create();
        }

        @Override
        public MaterialColorPickerDialog.Builder setTitle(int titleId) {
            super.setTitle(titleId);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setTitle(CharSequence title) {
            super.setTitle(title);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setCustomTitle(View customTitleView) {
            super.setCustomTitle(customTitleView);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setMessage(int messageId) {
            super.setMessage(getContext().getString(messageId));
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setMessage(CharSequence message) {
            super.setMessage(message);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setIcon(int iconId) {
            super.setIcon(iconId);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setIcon(Drawable icon) {
            super.setIcon(icon);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setIconAttribute(int attrId) {
            super.setIconAttribute(attrId);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setCancelable(boolean cancelable) {
            super.setCancelable(cancelable);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setOnCancelListener(OnCancelListener onCancelListener) {
            super.setOnCancelListener(onCancelListener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setOnDismissListener(OnDismissListener onDismissListener) {
            super.setOnDismissListener(onDismissListener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setOnKeyListener(OnKeyListener onKeyListener) {
            super.setOnKeyListener(onKeyListener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setPositiveButton(int textId, OnClickListener listener) {
            super.setPositiveButton(textId, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            super.setPositiveButton(text, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setNeutralButton(int textId, OnClickListener listener) {
            super.setNeutralButton(textId, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            super.setNeutralButton(text, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setItems(int itemsId, OnClickListener listener) {
            super.setItems(itemsId, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setItems(CharSequence[] items, OnClickListener listener) {
            super.setItems(items, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setAdapter(ListAdapter adapter, OnClickListener listener) {
            super.setAdapter(adapter, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setCursor(Cursor cursor, OnClickListener listener, String labelColumn) {
            super.setCursor(cursor, listener, labelColumn);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setMultiChoiceItems(
                int itemsId, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            super.setMultiChoiceItems(itemsId, checkedItems, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setMultiChoiceItems(
                CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            super.setMultiChoiceItems(items, checkedItems, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setMultiChoiceItems(
                Cursor cursor,
                String isCheckedColumn,
                String labelColumn,
                OnMultiChoiceClickListener listener) {
            super.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener) {
            super.setSingleChoiceItems(itemsId, checkedItem, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setSingleChoiceItems(
                Cursor cursor, int checkedItem, String labelColumn, OnClickListener listener) {
            super.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setSingleChoiceItems(
                CharSequence[] items, int checkedItem, OnClickListener listener) {
            super.setSingleChoiceItems(items, checkedItem, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setSingleChoiceItems(
                ListAdapter adapter, int checkedItem, OnClickListener listener) {
            super.setSingleChoiceItems(adapter, checkedItem, listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
            super.setOnItemSelectedListener(listener);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setView(int layoutResId) {
            super.setView(layoutResId);
            return this;
        }

        @Override
        public MaterialColorPickerDialog.Builder setView(View view) {
            super.setView(view);
            return this;
        }
    }
}
