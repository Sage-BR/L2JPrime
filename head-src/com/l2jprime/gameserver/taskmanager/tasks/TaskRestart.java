/* l2jprime Project - 4teambr.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jprime.gameserver.taskmanager.tasks;

import org.apache.log4j.Logger;

import com.l2jprime.gameserver.Shutdown;
import com.l2jprime.gameserver.taskmanager.Task;
import com.l2jprime.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author Layane
 */
public final class TaskRestart extends Task
{
	private static final Logger LOGGER = Logger.getLogger(TaskRestart.class);
	public static final String NAME = "restart";

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.tasks.Task#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see com.l2jprime.gameserver.tasks.Task#onTimeElapsed(com.l2jprime.gameserver.tasks.TaskManager.ExecutedTask)
	 */
	@Override
	public void onTimeElapsed(final ExecutedTask task)
	{
		LOGGER.info("[GlobalTask] Server Restart launched.");

		final Shutdown handler = new Shutdown(Integer.valueOf(task.getParams()[2]), true, true, false);
		handler.start();
	}
}