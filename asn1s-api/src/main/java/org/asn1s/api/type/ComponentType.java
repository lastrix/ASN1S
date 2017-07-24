////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2010-2017. Lapinin "lastrix" Sergey.                          /
//                                                                             /
// Permission is hereby granted, free of charge, to any person                 /
// obtaining a copy of this software and associated documentation              /
// files (the "Software"), to deal in the Software without                     /
// restriction, including without limitation the rights to use,                /
// copy, modify, merge, publish, distribute, sublicense, and/or                /
// sell copies of the Software, and to permit persons to whom the              /
// Software is furnished to do so, subject to the following                    /
// conditions:                                                                 /
//                                                                             /
// The above copyright notice and this permission notice shall be              /
// included in all copies or substantial portions of the Software.             /
//                                                                             /
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,             /
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES             /
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND                    /
// NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT                /
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                /
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING                /
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE                  /
// OR OTHER DEALINGS IN THE SOFTWARE.                                          /
////////////////////////////////////////////////////////////////////////////////

package org.asn1s.api.type;

import org.asn1s.api.Ref;
import org.asn1s.api.value.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings( "ConstantDeclaredInInterface" )
public interface ComponentType extends NamedType
{
	String DUMMY = "dummy";

	default boolean isDummy()
	{
		return DUMMY.equals( getName() );
	}

	@Nullable
	Value getDefaultValue();

	@Nullable
	Ref<Value> getDefaultValueRef();

	boolean isOptional();

	default boolean isRequired()
	{
		return !isOptional() && getDefaultValue() == null;
	}

	@NotNull
	default String getComponentName()
	{
		return getName();
	}

	@NotNull
	Type getComponentType();

	@NotNull
	Ref<Type> getComponentTypeRef();

	/**
	 * @return version of this component
	 */
	int getVersion();

	/**
	 * @return index in list of all components
	 */
	int getIndex();

	/**
	 * Returns true if this component is tagged type.
	 * This must not include cases where tagged type is referenced
	 *
	 * @return boolean
	 */
	boolean isExplicitlyTagged();

	@NotNull
	@Override
	ComponentType copy();

	enum Kind
	{
		Primary, // goes before extension
		Secondary, // goes after extension
		Extension
	}
}
