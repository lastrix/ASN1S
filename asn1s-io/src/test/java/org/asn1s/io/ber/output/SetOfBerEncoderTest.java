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

package org.asn1s.io.ber.output;

import org.asn1s.api.Scope;
import org.asn1s.api.UniversalType;
import org.asn1s.api.encoding.tag.Tag;
import org.asn1s.api.encoding.tag.TagClass;
import org.asn1s.api.exception.Asn1Exception;
import org.asn1s.api.type.ComponentType;
import org.asn1s.api.type.Type;
import org.asn1s.api.value.Value;
import org.asn1s.api.value.x680.BooleanValue;
import org.asn1s.api.value.x680.ValueCollection;
import org.asn1s.core.module.CoreModule;
import org.asn1s.core.type.x680.collection.SetOfType;
import org.asn1s.core.value.x680.IntegerValueInt;
import org.asn1s.core.value.x680.ValueCollectionImpl;
import org.asn1s.io.ber.BerRules;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetOfBerEncoderTest
{
	public static final Tag TAG = new Tag( TagClass.UNIVERSAL, true, UniversalType.SET.tagNumber() );

	@Test
	public void testWriteSetOf_Buffered() throws Exception
	{
		Scope scope = CoreModule.getInstance().createScope();
		SetOfType type = new SetOfType();
		type.setComponent( "a", UniversalType.INTEGER.ref() );
		type.validate( scope );
		ComponentType component = type.getComponentType();
		Assert.assertNotNull( "No component a", component );
		ValueCollection value = new ValueCollectionImpl( false );
		Value valueInt = new IntegerValueInt( 0 );
		value.add( valueInt );
		try( AbstractBerWriter writer = new DefaultBerWriter( BerRules.DER ) )
		{
			new SetOfBerEncoder().encode( new WriterContext( writer, scope, type, value, true ) );
			byte[] bytes = writer.toByteArray();
			Assert.assertArrayEquals( "Arrays are not equal", new byte[]{0x31, 0x03, 0x02, 0x01, 0x00}, bytes );
		}
	}

	@Test
	public void testWriteSetOf_NonBuffered() throws Exception
	{
		Scope scope = CoreModule.getInstance().createScope();
		SetOfType type = new SetOfType();
		type.setComponent( "a", UniversalType.INTEGER.ref() );
		type.validate( scope );
		ComponentType component = type.getComponentType();
		Assert.assertNotNull( "No component a", component );
		ValueCollection value = new ValueCollectionImpl( false );
		Value valueInt = new IntegerValueInt( 0 );
		value.add( valueInt );
		try( AbstractBerWriter writer = new DefaultBerWriter( BerRules.BER ) )
		{
			new SetOfBerEncoder().encode( new WriterContext( writer, scope, type, value, true )
			{
				@Override
				public boolean isBufferingAvailable()
				{
					return false;
				}
			} );
			byte[] bytes = writer.toByteArray();
			Assert.assertArrayEquals( "Arrays are not equal", new byte[]{0x31, (byte)0x80, 0x02, 0x01, 0x00, 0x00, 0x00}, bytes );
		}
	}

	@Test( expected = Asn1Exception.class )
	public void testWriteSetOf_NonBuffered_Der() throws Exception
	{
		Scope scope = CoreModule.getInstance().createScope();
		SetOfType type = new SetOfType();
		type.setComponent( "a", UniversalType.INTEGER.ref() );
		type.validate( scope );
		ComponentType component = type.getComponentType();
		Assert.assertNotNull( "No component a", component );
		ValueCollection value = new ValueCollectionImpl( false );
		Value valueInt = new IntegerValueInt( 0 );
		value.add( valueInt );
		try( AbstractBerWriter writer = mock( AbstractBerWriter.class ) )
		{
			when( writer.isBufferingAvailable() ).thenReturn( false );
			when( writer.getRules() ).thenReturn( BerRules.DER );
			new SetOfBerEncoder().encode( new WriterContext( writer, scope, type, value, true ) );
			fail( "Must fail" );
		}
	}

	@Test
	public void testWriteSetOf_NoHeader() throws Exception
	{
		Scope scope = CoreModule.getInstance().createScope();
		SetOfType type = new SetOfType();
		type.setComponent( "a", UniversalType.INTEGER.ref() );
		type.validate( scope );
		ComponentType component = type.getComponentType();
		Assert.assertNotNull( "No component a", component );
		ValueCollection value = new ValueCollectionImpl( false );
		Value valueInt = new IntegerValueInt( 0 );
		value.add( valueInt );
		try( AbstractBerWriter writer = new DefaultBerWriter( BerRules.DER ) )
		{
			new SetOfBerEncoder().encode( new WriterContext( writer, scope, type, value, false ) );
			byte[] bytes = writer.toByteArray();
			Assert.assertArrayEquals( "Arrays are not equal", new byte[]{0x02, 0x01, 0x00}, bytes );
		}
	}

	@Test( expected = AssertionError.class )
	public void testEncode_fail_type() throws Exception
	{
		Scope scope = CoreModule.getInstance().createScope();
		Type type = UniversalType.INTEGER.ref().resolve( scope );
		Value value = new ValueCollectionImpl( true );
		try( AbstractBerWriter writer = mock( AbstractBerWriter.class ) )
		{
			new SetOfBerEncoder().encode( new WriterContext( writer, scope, type, value, false ) );
			fail( "Must fail" );
		}
	}

	@Test( expected = AssertionError.class )
	public void testEncode_fail_value() throws Exception
	{
		Scope scope = CoreModule.getInstance().createScope();
		Type type = new SetOfType();
		Value value = BooleanValue.TRUE;
		try( AbstractBerWriter writer = mock( AbstractBerWriter.class ) )
		{
			new SetOfBerEncoder().encode( new WriterContext( writer, scope, type, value, false ) );
			fail( "Must fail" );
		}
	}

}
