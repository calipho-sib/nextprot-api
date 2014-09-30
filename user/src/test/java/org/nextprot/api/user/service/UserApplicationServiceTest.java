package org.nextprot.api.user.service;

import org.junit.Ignore;
import org.nextprot.api.commons.dbunit.CommonsUnitBaseTest;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@Ignore
@DatabaseSetup(value = "DbXrefServiceTest.xml", type = DatabaseOperation.INSERT)
public class UserApplicationServiceTest extends CommonsUnitBaseTest {

}
