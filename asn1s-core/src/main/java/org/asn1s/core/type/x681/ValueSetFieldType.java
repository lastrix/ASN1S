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

package org.asn1s.core.type.x681;

import org.asn1s.api.Ref;
import org.asn1s.api.Scope;
import org.asn1s.api.constraint.ConstraintTemplate;
import org.asn1s.api.exception.ConstraintViolationException;
import org.asn1s.api.exception.IllegalValueException;
import org.asn1s.api.exception.ResolutionException;
import org.asn1s.api.exception.ValidationException;
import org.asn1s.api.type.Type;
import org.asn1s.api.type.x681.AbstractFieldTypeWithDefault;
import org.asn1s.api.util.RefUtils;
import org.asn1s.api.value.Value;
import org.asn1s.core.type.ConstrainedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ValueSetFieldType extends AbstractFieldTypeWithDefault<Type>
{
	public ValueSetFieldType( @NotNull String name, @NotNull Ref<Type> fieldTypeRef, boolean optional, @Nullable ConstraintTemplate defaultElementSetSpecs )
	{
		super( name, fieldTypeRef, optional );
		this.defaultElementSetSpecs = defaultElementSetSpecs;
		RefUtils.assertTypeRef( name.substring( 1 ) );

		if( defaultElementSetSpecs != null )
			setDefaultRef( new ConstrainedType( defaultElementSetSpecs, fieldTypeRef ) );
	}

	@Nullable
	private final ConstraintTemplate defaultElementSetSpecs;

	@Override
	protected void onValidate( @NotNull Scope scope ) throws ResolutionException, ValidationException
	{
		scope = getScope( scope );
		super.onValidate( scope );

		if( hasDefault() )
		{
			setDefault( getDefaultRef().resolve( scope ) );
			//noinspection ConstantConditions
			getDefault().validate( scope );
		}
	}

	@Override
	public void acceptRef( @NotNull Scope scope, Ref<Type> ref ) throws ResolutionException, IllegalValueException, ConstraintViolationException
	{
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public Value optimize( @NotNull Scope scope, @NotNull Ref<Value> valueRef ) throws ResolutionException, ValidationException
	{
		if( hasSibling() )
			return getSibling().optimize( scope, valueRef );

		if( hasDefault() )
			//noinspection ConstantConditions
			return getDefault().optimize( scope, valueRef );
		throw new ResolutionException( "No validator defined" );
	}

	@Override
	public Type optimizeRef( @NotNull Scope scope, Ref<Type> ref ) throws ResolutionException, ValidationException
	{
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public Type copy()
	{
		return new ValueSetFieldType( getName(), cloneSibling(), isOptional(), defaultElementSetSpecs );
	}

	@Override
	public Kind getClassFieldKind()
	{
		return Kind.VALUE_SET;
	}
}
