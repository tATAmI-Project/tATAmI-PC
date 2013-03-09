/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package tatami.core.util;

/**
 * The Config is to be used as a base class / paradigm for any construction-time configuration that needs a large number of parameters and considers optional
 * and default parameters.
 * 
 * <p>
 * A mandatory configuration is a set of parameters that are absolutely necessary for the configuration. There may be more such sets, and they may intersect.
 * 
 * * <p>
 * In case of a config with ancestors, like RootConfig -> ChildConfig -> CurrentConfig, since a setter returns an instance of its class (which may not be the
 * original calling class), chained setters should be called for convenience in the order:
 * <code>currentConfigInstance.setParamInCurrentConfig().setOtherParamInCurrentConfig().setParamInChildConfig().setParamInRootConfig()</code> Obviously, a
 * RootConfig instance will be returned in the end, but that can be casted back to CurrentConfig.
 * 
 * <p>
 * Rules
 * <ul>
 * <li>There are setter functions for all changeable parameters.
 * <li>All setters return <code>this</code>.
 * <li>If there are mandatory parameters, there is a constructor that takes these parameters - the primary constructor.
 * <li>If there is no mandatory parameter, there is a default constructor (i.e. Config() )
 * <li>The primary constructor takes all parameters that can be mandatory. If there is no mandatory configuration that takes all potentially mandatory
 * parameters, the primary constructor is protected. Otherwise, it is public.
 * <li>All other constructors call the primary constructor.
 * <li>Preferably constructors should be used only for mandatory parameters. Anything else should be done by setters.
 * <li>Only the primary constructor calls the super() constructor.
 * <li>Remember that all initialization of the fields will be done <i>after</i> the call of makeDefaults()
 * <li>makeDefaults() should <i>always</i> begin by calling super.makeDefaults(), and then making necessary adjustments.
 * <li>makeDefaults() should only affect fields (from the ancestor configs) that have a default value for the configured object that is relevant to the
 * configured object (and not to the ancestor).
 * <li>Setting of changeable parameters (either by constructors, makeDefaults(), or other setters) will <i>always</i> be done by calling the appropriate setter
 * function.
 * <li>If the configured object inherits from an ancestor class, the Config should inherit the Config of the ancestor class (if any).
 * </ul>
 * 
 * @author Andrei Olaru
 * 
 */
public abstract class Config
{
	public Config()
	{
		makeDefaults();
	}
	
	public Config makeDefaults()
	{
		return this;
	}
}
