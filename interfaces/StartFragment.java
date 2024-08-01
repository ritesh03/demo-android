package com.maktoday.interfaces;

import com.joooonho.SelectableRoundedImageView;
import com.maktoday.model.MaidData;

/**
 * Created by cbl81 on 17/11/17.
 */

public interface StartFragment {
    void startIntent(MaidData id, SelectableRoundedImageView ivMaid, int adapterPosition);
}
