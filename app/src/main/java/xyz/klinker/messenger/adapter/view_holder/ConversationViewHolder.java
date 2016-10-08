/*
 * Copyright (C) 2016 Jacob Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.klinker.messenger.adapter.view_holder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.klinker.messenger.R;
import xyz.klinker.messenger.adapter.ConversationListAdapter;
import xyz.klinker.messenger.data.Settings;
import xyz.klinker.messenger.data.model.Conversation;
import xyz.klinker.messenger.util.AnimationUtils;
import xyz.klinker.messenger.util.listener.ContactClickedListener;
import xyz.klinker.messenger.util.listener.ConversationExpandedListener;

/**
 * View holder for recycling inflated conversations.
 */
public class ConversationViewHolder extends RecyclerView.ViewHolder {

    public View headerBackground;
    public TextView header;
    public ImageButton headerDone;
    public CircleImageView image;
    public TextView name;
    public TextView summary;
    public TextView imageLetter;
    public View unreadIndicator;
    public CheckBox checkBox;

    public Conversation conversation;
    public int position = -1;

    private boolean expanded = false;
    private ConversationExpandedListener expandedListener;
    private ContactClickedListener contactClickedListener;

    public ConversationViewHolder(View itemView, final ConversationExpandedListener listener, final ConversationListAdapter adapter) {
        super(itemView);

        this.position = -1;
        this.expandedListener = listener;

        headerBackground = itemView.findViewById(R.id.header_background);
        header = (TextView) itemView.findViewById(R.id.header);
        headerDone = (ImageButton) itemView.findViewById(R.id.section_done);
        image = (CircleImageView) itemView.findViewById(R.id.image);
        name = (TextView) itemView.findViewById(R.id.name);
        summary = (TextView) itemView.findViewById(R.id.summary);
        imageLetter = (TextView) itemView.findViewById(R.id.image_letter);
        unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conversation == null) {
                    return;
                }

                if (header == null) {
                    if (position != -1 && adapter != null && position < adapter.getConversations().size()) {
                        adapter.getConversations().get(position).read = true;
                    }

                    setTypeface(false, isItalic());
                }

                if (listener != null) {
                    changeExpandedState();
                }

                if (contactClickedListener != null) {
                    contactClickedListener.onClicked(
                            conversation.title, conversation.phoneNumbers, conversation.imageUri);
                }

                if (checkBox != null) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        });

        Settings settings = Settings.get(itemView.getContext());

        if (header != null) {
            header.setTextSize(settings.smallFont);
        }

        if (name!= null && summary != null) {
            name.setTextSize(settings.largeFont);
            summary.setTextSize(settings.mediumFont);
        }

        if (settings.baseTheme == Settings.BaseTheme.BLACK && headerBackground != null) {
            headerBackground.setBackgroundColor(Color.BLACK);
        }
    }

    public boolean isBold() {
        return name.getTypeface() != null && name.getTypeface().isBold();
    }

    public boolean isItalic() {
        return name.getTypeface() != null && name.getTypeface().getStyle() == Typeface.ITALIC;
    }

    public void setTypeface(boolean bold, boolean italic) {
        if (bold) {
            name.setTypeface(Typeface.DEFAULT_BOLD, italic ? Typeface.ITALIC : Typeface.NORMAL);
            summary.setTypeface(Typeface.DEFAULT_BOLD, italic ? Typeface.ITALIC : Typeface.NORMAL);

            if (unreadIndicator != null) {
                unreadIndicator.setVisibility(View.VISIBLE);
            }

            ((CircleImageView) unreadIndicator).setImageDrawable(new ColorDrawable(Settings.get(itemView.getContext()).globalColorSet.color));
        } else {
            name.setTypeface(Typeface.DEFAULT, italic ? Typeface.ITALIC : Typeface.NORMAL);
            summary.setTypeface(Typeface.DEFAULT, italic ? Typeface.ITALIC : Typeface.NORMAL);

            if (unreadIndicator != null) {
                unreadIndicator.setVisibility(View.GONE);
            }
        }
    }

    private void changeExpandedState() {
        if (header != null) {
            return;
        }

        if (expanded) {
            collapseConversation();
        } else {
            expandConversation();
        }
    }

    private void expandConversation() {
        if (expandedListener.onConversationExpanded(this)) {
            expanded = true;
            AnimationUtils.expandConversationListItem(itemView);
        }
    }

    private void collapseConversation() {
        expanded = false;
        expandedListener.onConversationContracted(this);
        AnimationUtils.contractConversationListItem(itemView);
    }

    public void setContactClickedListener(ContactClickedListener listener) {
        this.contactClickedListener = listener;
    }
}
