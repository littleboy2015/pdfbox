/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * This will test the functionality of choice fields in PDFBox.
 */
public class TestListBox extends TestCase
{
    
    /**
     * Constructor.
     *
     * @param name The name of the test to run.
     */
    public TestListBox( String name )
    {
        super( name );
    }

    /**
     * This will get the suite of test that this class holds.
     *
     * @return All of the tests that this class holds.
     */
    public static Test suite()
    {
        return new TestSuite( TestListBox.class );
    }

    /**
     * infamous main method.
     *
     * @param args The command line arguments.
     */
    public static void main( String[] args )
    {
        String[] arg = {TestListBox.class.getName() };
        junit.textui.TestRunner.main( arg );
    }

    /**
     * This will test the radio button PDModel.
     *
     * @throws IOException If there is an error creating the field.
     */
    public void testChoicePDModel() throws IOException
    {
        PDDocument doc = null;
        try
        {
            doc = new PDDocument();
            PDAcroForm form = new PDAcroForm( doc );
            PDChoice choice = new PDListBox(form);
            
            // test that there are no nulls returned for an empty field
            // only specific methods are tested here
            // assertNotNull(choice.getDefaultValue());
            assertNotNull(choice.getOptions());
            assertNotNull(choice.getValue());
            
            // define display and export values as the choice field can hold a pair
            List<String> exportValues = new ArrayList<String>();
            exportValues.add("export01");
            exportValues.add("export02");

            List<String> displayValues = new ArrayList<String>();
            displayValues.add("display02");
            displayValues.add("display01");
            
            // test with exportValue being set

            // setting/getting option values - the dictionaries Opt entry
            choice.setOptions(exportValues);
            assertEquals(exportValues,choice.getOptionsDisplayValues());
            assertEquals(exportValues,choice.getOptionsExportValues());

            COSArray optItem = (COSArray) choice.getDictionary().getItem(COSName.OPT);
            
            // assert that the values have been correctly set
            assertNotNull(choice.getDictionary().getItem(COSName.OPT));
            assertEquals(optItem.size(),2);
            assertEquals(exportValues.get(0), optItem.getString(0));
            
            // assert that the values can be retrieved correctly
            List<String> retrievedOptions = choice.getOptions();
            assertEquals(retrievedOptions.size(),2);
            assertEquals(retrievedOptions, exportValues);

            // assert that the Opt entry is removed
            choice.setOptions(null);
            assertNull(choice.getDictionary().getItem(COSName.OPT));
            // if there is no Opt entry an empty List shall be returned
            assertEquals(choice.getOptions(), Collections.<String>emptyList());
            
            // test with exportValue and displayValue being set
            
            // setting display and export value
            choice.setOptions(exportValues, displayValues);
            assertEquals(displayValues,choice.getOptionsDisplayValues());
            assertEquals(exportValues,choice.getOptionsExportValues());
            
            // testing the sort option
            assertEquals(choice.getOptionsDisplayValues().get(0),"display02");
            choice.setSort(true);
            choice.setOptions(exportValues, displayValues);
            assertEquals(choice.getOptionsDisplayValues().get(0),"display01");
            
            // assert that the Opt entry is removed
            choice.setOptions(null, displayValues);
            assertNull(choice.getDictionary().getItem(COSName.OPT));
            // if there is no Opt entry an empty List shall be returned
            assertEquals(choice.getOptions(), Collections.<String>emptyList());
            assertEquals(choice.getOptionsDisplayValues(), Collections.<String>emptyList());
            assertEquals(choice.getOptionsExportValues(), Collections.<String>emptyList());
            
            // test that an IllegalArgumentException is thrown when export and display 
            // value lists have different sizes
            exportValues.remove(1);
            
            try
            {
                choice.setOptions(exportValues, displayValues);
                fail( "Missing exception" );
            }
            catch( IllegalArgumentException e )
            {
                assertEquals( 
                        "The number of entries for exportValue and displayValue shall be the same.",
                        e.getMessage() );
            }
        }
        finally
        {
            if( doc != null )
            {
                doc.close();
            }
        }
    }
}