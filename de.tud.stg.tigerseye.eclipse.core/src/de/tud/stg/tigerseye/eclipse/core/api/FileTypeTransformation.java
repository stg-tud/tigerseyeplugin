package de.tud.stg.tigerseye.eclipse.core.api;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tud.stg.tigerseye.eclipse.core.builder.transformers.FileType;

public class FileTypeTransformation extends AbstractTransformatinType {

    private final FileType ft;

    public FileTypeTransformation(FileType ft, IPreferenceStore store) {
	super(store);
	this.ft = ft;
    }

    @Override
    public boolean getDefaultActiveFor(ITransformationHandler handler) {
	return handler.supports(ft);
    }

    @Override
    public String getIdentifier() {
	return ft.getClass().getName() + ft.name + "TransformationContext";
    }

}
