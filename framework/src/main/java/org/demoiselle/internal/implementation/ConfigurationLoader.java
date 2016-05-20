/*
/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package org.demoiselle.internal.implementation;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConversionException;
import org.demoiselle.annotation.Ignore;
import org.demoiselle.annotation.Name;
import org.demoiselle.annotation.literal.NameQualifier;
import org.demoiselle.configuration.ConfigType;
import org.demoiselle.configuration.ConfigurationException;
import org.demoiselle.configuration.ConfigurationValueExtractor;
import org.demoiselle.internal.bootstrap.ConfigurationBootstrap;
import org.demoiselle.internal.producer.StrategySelector;
import org.demoiselle.util.Reflections;
import org.demoiselle.util.ResourceBundle;
import org.demoiselle.util.Strings;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Named;
import javax.validation.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.demoiselle.configuration.ConfigType.SYSTEM;

/**
 * This component loads a config class annotated with {@link org.demoiselle.configuration.Configuration}
 * by filling its attributes with {@link org.demoiselle.jsf.util.Parameter}
 * according to a {@link org.demoiselle.configuration.ConfigType}.
 *
 * @author SERPRO
 */
@ApplicationScoped
@Named("demoiselle-configuration-loader")
public class ConfigurationLoader implements Serializable {

	private static final long serialVersionUID = 1L;

	private ResourceBundle bundle;

	private Logger logger;

	private Object object;

	private ConfigType type;

	private String resource;

	private String prefix;

	private Configuration configuration;

	private Collection<Field> fields;

	private final Object lock = new Object();

	private final Map<Object, Boolean> loadedCache = new ConcurrentHashMap<>();

	public void load(final Object object) throws ConfigurationException {
		Boolean isLoaded = loadedCache.get(object);

		if (isLoaded == null || !isLoaded) {
			try {
				loadConfiguration(object, true);
				loadedCache.put(object, true);
			} catch (ConfigurationException c) {
				loadedCache.put(object, false);
				throw c;
			}
		}
	}

	public void load(final Object object, boolean logLoadingProcess) throws ConfigurationException {
		Boolean isLoaded = loadedCache.get(object);

		if (isLoaded == null || !isLoaded) {
			try {
				loadConfiguration(object, logLoadingProcess);
				loadedCache.put(object, true);
			} catch (ConfigurationException c) {
				loadedCache.put(object, false);
				throw c;
			}
		}
	}

	private synchronized void loadConfiguration(final Object object, boolean logLoadingProcess)
			throws ConfigurationException {
		if (logLoadingProcess) {
			getLogger().fine(getBundle().getString("loading-configuration-class", object.getClass().getName()));
		}

		this.object = object;

		loadFields();
		validateFields();

		loadType();
		loadResource();
		loadConfiguration();

		if (this.configuration != null) {
			loadPrefix();
			loadValues();
		}

		validateValues();
	}

	private void loadFields() {
		this.fields = Reflections.getNonStaticFields(this.object.getClass());
	}

	private void validateFields() {
		this.fields.forEach(this::validateField);
	}

	private void validateField(Field field) {
		Name annotation = field.getAnnotation(Name.class);

		if (annotation != null && Strings.isEmpty(annotation.value())) {
			throw new ConfigurationException(getBundle().getString("configuration-name-attribute-cant-be-empty"),
					new IllegalArgumentException());
		}
	}

	private void loadType() {
		this.type = object.getClass().getAnnotation(org.demoiselle.configuration.Configuration.class).type();
	}

	private void loadResource() {
		if (this.type != SYSTEM) {
			String name = this.object.getClass().getAnnotation(org.demoiselle.configuration.Configuration.class)
					.resource();
			String extension = this.type.toString().toLowerCase();

			this.resource = name + "." + extension;
		}
	}

	private void loadConfiguration() {
		Configuration config;
		BasicConfigurationBuilder<? extends Configuration> builder = createConfiguration();

		if (builder instanceof FileBasedConfigurationBuilder) {
			Parameters params = new Parameters();

			((FileBasedConfigurationBuilder) builder)
					.configure(params.fileBased().setURL(Reflections.getResourceAsURL(this.resource)));
		}

		try {
			config = builder.getConfiguration();
		} catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
			getLogger().warning(getBundle().getString("file-not-found", this.resource));
			config = null;
		}

		this.configuration = config;
	}

	private BasicConfigurationBuilder<? extends Configuration> createConfiguration() {
		BasicConfigurationBuilder<? extends Configuration> builder;

		switch (this.type) {
			case XML:
				builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(XMLConfiguration.class);
				break;

			case SYSTEM:
				builder = new BasicConfigurationBuilder<>(SystemConfiguration.class);
				break;

			default:
				builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class);
		}

		return builder;
	}

	private void loadPrefix() {
		String prefix = this.object.getClass().getAnnotation(org.demoiselle.configuration.Configuration.class).prefix();

		if (prefix.endsWith(".")) {
			getLogger().warning(getBundle().getString("configuration-dot-after-prefix", this.resource));
		} else if (!prefix.isEmpty()) {
			prefix += ".";
		}

		this.prefix = prefix;
	}

	private void loadValues() {
		this.fields.forEach(this::loadValue);
	}

	private void loadValue(Field field) {
		if (hasIgnore(field)) {
			return;
		}

		Object defaultValue = Reflections.getFieldValue(field, this.object);
		Object loadedValue = getValue(field, field.getType(), getKey(field), defaultValue);
		Object finalValue = (loadedValue == null ? defaultValue : loadedValue);

		if (loadedValue == null) {
			getLogger().fine(getBundle().getString("configuration-key-not-found", this.prefix + getKey(field)));
		}

		Reflections.setFieldValue(field, this.object, finalValue);
		getLogger().finer(getBundle()
				.getString("configuration-field-loaded", this.prefix + getKey(field), field.getName(),
						finalValue == null ? "null" : finalValue));
	}

	private Object getValue(Field field, Class<?> type, String key, Object defaultValue) {
		Object value;

		try {
			ConfigurationValueExtractor extractor = getValueExtractor(field);
			value = extractor.getValue(this.prefix, key, field, this.configuration);

		} catch (ConfigurationException cause) {
			throw cause;

		} catch (ConversionException cause) {
			throw new ConfigurationException(getBundle()
					.getString("configuration-not-conversion", this.prefix + getKey(field), field.getType().toString()),
					cause);
		} catch (Exception cause) {
			throw new ConfigurationException(getBundle()
					.getString("configuration-generic-extraction-error", field.getType().toString(),
							getValueExtractor(field).getClass().getCanonicalName()), cause);
		}

		return value;
	}

	private ConfigurationValueExtractor getValueExtractor(Field field) {
		Collection<ConfigurationValueExtractor> candidates = new HashSet<ConfigurationValueExtractor>();
		ConfigurationBootstrap bootstrap = CDI.current().select(ConfigurationBootstrap.class).get();

		for (Class<? extends ConfigurationValueExtractor> extractorClass : bootstrap.getCache()) {
			ConfigurationValueExtractor extractor = CDI.current().select(extractorClass).get();

			if (extractor.isSupported(field)) {
				candidates.add(extractor);
			}
		}

		ConfigurationValueExtractor elected = StrategySelector
				.selectReference(ConfigurationValueExtractor.class, candidates);

		if (elected == null) {
			throw new ConfigurationException(getBundle()
					.getString("configuration-extractor-not-found", field.toGenericString(),
							ConfigurationValueExtractor.class.getName()), new ClassNotFoundException());
		}

		return elected;
	}

	private String getKey(Field field) {
		String key;

		if (field.isAnnotationPresent(Name.class)) {
			key = field.getAnnotation(Name.class).value();
		} else {
			key = field.getName();
		}

		return key;
	}

	private boolean hasIgnore(Field field) {
		return field.isAnnotationPresent(Ignore.class);
	}

	private void validateValues() {
		for (Field field : this.fields) {
			validateValue(field, Reflections.getFieldValue(field, this.object));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void validateValue(Field field, Object value) {
		ValidatorFactory dfv = Validation.buildDefaultValidatorFactory();
		Validator validator = dfv.getValidator();

		Set violations = validator.validateProperty(this.object, field.getName());

		StringBuilder message = new StringBuilder();

		if (!violations.isEmpty()) {
			for (Iterator iter = violations.iterator(); iter.hasNext(); ) {
				ConstraintViolation violation = (ConstraintViolation) iter.next();
				message.append(field.toGenericString() + " " + violation.getMessage() + "\n");
			}

			throw new ConfigurationException(message.toString(), new ConstraintViolationException(violations));
		}
	}

	private ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = CDI.current().select(ResourceBundle.class, new NameQualifier("demoiselle-core-bundle")).get();
		}

		return bundle;
	}

	private Logger getLogger() {
		if (logger == null) {
			logger = CDI.current().select(Logger.class, new NameQualifier("br.gov.frameworkdemoiselle.configuration"))
					.get();
		}

		return logger;
	}
}
