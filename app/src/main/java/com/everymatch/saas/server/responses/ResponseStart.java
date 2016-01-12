package com.everymatch.saas.server.responses;


import java.io.Serializable;

public class ResponseStart extends BaseResponse implements Serializable {

    String name;
    WalkthroughData[] model;
    DesignData design;

    public DesignData getDesign() {
        return design;
    }

    public void setDesign(DesignData design) {
        this.design = design;
    }

    public WalkthroughData[] getModel() {
        return model;
    }

    public void setModel(WalkthroughData[] model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public class WalkthroughData implements Serializable {
        public String text_title;
        public String text_subtitle;
        public String text_description;
        public String background_image;
        public String icon_image_url;
        public String icon_font;
    }

    private class DesignData {
        String primary_color;
        String secondary_color;
        String tertiary_color;
        String font_color;
        String font_size;
        String font_family;
        String link_color;
        String display;
        String background_color;
        String background_image;
        String custom_css;
    }


//    template: {
//        name: "splash"
//    },
//    placeholder: "placeholder1",
//    layout_model: {
//        id: "splash-1"
//    },


}
