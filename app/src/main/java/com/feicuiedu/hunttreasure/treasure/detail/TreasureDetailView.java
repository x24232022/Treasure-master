package com.feicuiedu.hunttreasure.treasure.detail;

import java.util.List;

/**
 * Created by Administrator on 2017/1/11 0011.
 */

public interface TreasureDetailView {
    void showMessage(String msg);
    void setData(List<TreasureDetailResult> resultList);
}
