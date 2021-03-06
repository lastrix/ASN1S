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

package org.asn1s.core.scope;

import org.asn1s.api.Ref;
import org.asn1s.api.Scope;
import org.asn1s.api.exception.ResolutionException;
import org.asn1s.api.type.DefinedType;
import org.asn1s.api.type.Type;
import org.asn1s.api.type.TypeName;
import org.asn1s.api.value.Value;
import org.asn1s.api.value.ValueName;
import org.asn1s.api.value.x680.NamedValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TypedScope extends AbstractScope
{
	TypedScope( Scope scope, Type type )
	{
		super( scope );
		this.type = type;
	}

	private final Type type;

	public Type getType()
	{
		return type;
	}

	@Override
	public Type getTypeOrDie()
	{
		return type;
	}

	@NotNull
	@Override
	public Ref<Type> getTypeRef( @NotNull String ref, @Nullable String module )
	{
		return getParentScope().getTypeRef( ref, module );
	}

	@NotNull
	@Override
	public Ref<Value> getValueRef( @NotNull String ref, @Nullable String module )
	{
		if( module == null )
		{
			NamedValue namedValue = type.getNamedValue( ref );
			if( namedValue != null )
				return namedValue;
		}
		return getParentScope().getValueRef( ref, module );
	}

	@Override
	public Type resolveType( @NotNull TypeName typeName ) throws ResolutionException
	{
		return getParentScope().resolveType( typeName );
	}

	@Override
	public Value resolveValue( @NotNull ValueName valueName ) throws ResolutionException
	{
		if( valueName.getModuleName() == null )
		{
			NamedValue namedValue = type.getNamedValue( valueName.getName() );
			if( namedValue != null )
				return namedValue;
		}
		return getParentScope().resolveValue( valueName );
	}

	@Override
	protected void fillValueLevels( Type[] types, Value[] values, int depth )
	{
		if( getValueLevel() != null )
		{
			types[depth - 1] = type;
			values[depth - 1] = getValueLevel();
			depth--;
		}

		if( depth > 0 && !( type instanceof DefinedType ) )
			super.fillValueLevels( types, values, depth );
	}

	@Override
	protected int getValueLevelDepth()
	{
		if( type instanceof DefinedType )
			return getValueLevel() == null ? 0 : 1;

		if( getValueLevel() == null )
			return super.getValueLevelDepth();

		return 1 + super.getValueLevelDepth();
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj ) return true;
		if( !( obj instanceof TypedScope ) ) return false;

		TypedScope scope = (TypedScope)obj;

		return getType().equals( scope.getType() );
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}
}
