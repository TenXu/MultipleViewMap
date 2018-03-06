package com.seekhoney.library_gdmap.component;

import com.seekhoney.library_gdmap.module.CommonModule;
import com.seekhoney.library_gdmap.scope.ActivityScope;
import com.seekhoney.library_gdmap.ui.GdmapActivity;
import dagger.Component;

/**
 * Created by ryan on 18/2/21.
 */
@ActivityScope
@Component (modules = CommonModule.class)
public interface CommonComponent {

    void inject(GdmapActivity activity);
}
