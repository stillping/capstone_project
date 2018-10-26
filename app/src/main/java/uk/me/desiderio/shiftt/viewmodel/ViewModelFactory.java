package uk.me.desiderio.shiftt.viewmodel;

import java.util.Map;

import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory class to provide {@link ViewModel}. The implemention follows guidelines so that can be
 * injected using Dagger
 */
public class ViewModelFactory<M extends ViewModel> extends ViewModelProvider.NewInstanceFactory {

    private Map<Class<M>, Provider<M>> creators;

    public ViewModelFactory(Map<Class<M>, Provider<M>> creators) {
        this.creators = creators;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<M> creator = creators.get(modelClass);

        if(creator == null) {
            throw new IllegalArgumentException("unknown model class " + modelClass.getSimpleName());
        }

        return (T)creator.get();

    }
}
