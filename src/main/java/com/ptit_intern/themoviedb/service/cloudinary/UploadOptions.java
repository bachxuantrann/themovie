package com.ptit_intern.themoviedb.service.cloudinary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UploadOptions {
    private String folder;
    private List<String> tags;
    private boolean overwrite=true;

    public Map<String,Object> toMap(){
        Map<String, Object> options = new HashMap<>();
        if (folder != null && !folder.isBlank()) {
            options.put("folder", folder);
        }
        if (tags != null && !tags.isEmpty()) {
            options.put("tags", tags);
        }
        options.put("overwrite", overwrite);
        return options;
    }
}
