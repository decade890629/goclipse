/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.googlecode.goclipse.ui.preferences;

import java.io.File;

import org.eclipse.swt.widgets.Composite;

import com.googlecode.goclipse.core.GoEnvironmentPrefs;
import com.googlecode.goclipse.tooling.GoSDKLocationValidator;

import melnorme.lang.ide.ui.preferences.LangSDKConfigBlock;
import melnorme.lang.ide.ui.preferences.common.PreferencesPageContext;
import melnorme.lang.tooling.ops.util.PathValidator;
import melnorme.lang.utils.EnvUtils;
import melnorme.util.swt.components.fields.CheckBoxField;
import melnorme.util.swt.components.fields.DirectoryTextField;
import melnorme.util.swt.components.fields.EnablementButtonTextField;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;

public class GoSDKConfigBlock extends LangSDKConfigBlock {
	
	protected final GoPathGroup goPathGroup = new GoPathGroup();
	
	public GoSDKConfigBlock(PreferencesPageContext prefContext) {
		super(prefContext);
		
		addSubComponent(goPathGroup);
	}
	
	@Override
	protected PathValidator getSDKValidator() {
		return new GoSDKLocationValidator();
	}
	
	@Override
	protected LanguageSDKLocationGroup init_createSDKLocationGroup() {
		return new LanguageSDKLocationGroup() {
			@Override
			protected void initBindings() {
				prefContext.bindToPreference(sdkLocationField, GoEnvironmentPrefs.GO_ROOT);
				validation.addFieldValidation(true, sdkLocationField, getSDKValidator());
			}
		};
	}
	

	/* -----------------  ----------------- */
	
	public class GoPathGroup extends EnablementButtonTextField {
		
		protected final CheckBoxField gopathAppendProjectLocField = new CheckBoxField(
				"Also add project location to GOPATH, if it's not contained there already.");
		
		public GoPathGroup() {
			super("Eclipse GOPATH:", "Use same value as the GOPATH environment variable.", "Add Folder");
			
			prefContext.bindToPreference(asEffectiveValueProperty2(), GoEnvironmentPrefs.GO_PATH);
			
			prefContext.bindToPreference(gopathAppendProjectLocField, GoEnvironmentPrefs.APPEND_PROJECT_LOC_TO_GOPATH);
		}
		
		@Override
		protected void createContents_all(Composite topControl) {
			super.createContents_all(topControl);
			gopathAppendProjectLocField.createComponentInlined(topControl);
		}
		
		@Override
		protected void doSetEnabled(boolean enabled) {
			super.doSetEnabled(enabled);
			gopathAppendProjectLocField.setEnabled(enabled);
		}
		
		@Override
		protected String getDefaultFieldValue() throws CommonException {
			return EnvUtils.getVarFromEnvMap(System.getenv(), "GOPATH");
		}
		
		@Override
		protected String getNewValueFromButtonSelection2() throws CommonException, OperationCancellation {
			String newValue = DirectoryTextField.openDirectoryDialog("", text.getShell());
			return getFieldValue() + File.pathSeparator + newValue;
		}
		
	}
	
}