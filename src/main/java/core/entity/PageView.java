package core.entity;

import java.util.List;

/**
 * @Auther: geguofeng
 * @Date: 2019/1/5
 * @Description:
 */
public class PageView {

    private int totalCount;

    private List<?> datas;

    private String dataJson;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<?> getDatas() {
        return datas;
    }

    public void setDatas(List<?> datas) {
        this.datas = datas;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
