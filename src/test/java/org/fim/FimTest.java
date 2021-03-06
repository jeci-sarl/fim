/*
 * This file is part of Fim - File Integrity Manager
 *
 * Copyright (C) 2015  Etienne Vrignaud
 *
 * Fim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fim.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fim;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.fim.command.exception.BadFimUsageException;
import org.fim.model.Context;
import org.fim.model.HashMode;
import org.fim.tooling.RepositoryTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class FimTest
{
	private static Path rootDir = Paths.get("target/" + FullScenarioTest.class.getSimpleName());

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	private Fim cut;
	private RepositoryTool tool;
	private Context context;

	@Before
	public void setUp() throws IOException
	{
		FileUtils.deleteDirectory(rootDir.toFile());
		Files.createDirectories(rootDir);

		cut = new Fim();

		tool = new RepositoryTool(rootDir);
		context = tool.createContext(HashMode.hashAll, true);
	}

	@Test(expected = BadFimUsageException.class)
	public void fimRepositoryAlreadyExist() throws Exception
	{
		initRepoAndCreateOneFile();
		cut.run(new String[]{"init", "-y"}, context);
	}

	@Test(expected = BadFimUsageException.class)
	public void fimDoesNotExist() throws Exception
	{
		cut.run(new String[]{"diff"}, context);
	}

	@Test
	public void weCanPrintUsage() throws Exception
	{
		exit.expectSystemExitWithStatus(0);
		Fim.main(new String[]{"help"});
	}

	@Test
	public void invalidOptionIsDetected() throws Exception
	{
		exit.expectSystemExitWithStatus(-1);
		Fim.main(new String[]{"-9"});
	}

	@Test
	public void weCanCommitUsingFim() throws Exception
	{
		initRepoAndCreateOneFile();
		cut.run(new String[]{"ci", "-y"}, context);
	}

	@Test
	public void doNotHashOption() throws Exception
	{
		initRepoAndCreateOneFile();
		cut.run(new String[]{"diff", "-n"}, context);
	}

	@Test
	public void fastModeOption() throws Exception
	{
		initRepoAndCreateOneFile();
		cut.run(new String[]{"diff", "-f"}, context);
	}

	@Test
	public void superFastModeOption() throws Exception
	{
		initRepoAndCreateOneFile();
		cut.run(new String[]{"diff", "-s"}, context);
	}

	@Test
	public void weCanRunVersionCommand() throws Exception
	{
		cut.run(new String[]{"-v"}, context);
	}

	@Test
	public void weCanRunHelpCommand() throws Exception
	{
		cut.run(new String[]{"-h"}, context);
	}

	@Test(expected = BadFimUsageException.class)
	public void noArgumentSpecified() throws Exception
	{
		cut.run(new String[]{""}, context);
	}

	@Test(expected = BadFimUsageException.class)
	public void noCommandSpecified() throws Exception
	{
		cut.run(new String[]{"-s"}, context);
	}

	private void initRepoAndCreateOneFile() throws Exception
	{
		cut.run(new String[]{"init", "-y"}, context);
		tool.createOneFile();
	}
}
