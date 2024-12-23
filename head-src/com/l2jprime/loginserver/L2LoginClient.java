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
package com.l2jprime.loginserver;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;

import org.apache.log4j.Logger;

import com.l2jprime.Config;
import com.l2jprime.crypt.LoginCrypt;
import com.l2jprime.crypt.ScrambledKeyPair;
import com.l2jprime.loginserver.network.serverpackets.L2LoginServerPacket;
import com.l2jprime.loginserver.network.serverpackets.LoginFail;
import com.l2jprime.loginserver.network.serverpackets.LoginFail.LoginFailReason;
import com.l2jprime.loginserver.network.serverpackets.PlayFail;
import com.l2jprime.loginserver.network.serverpackets.PlayFail.PlayFailReason;
import com.l2jprime.logs.Log;
import com.l2jprime.netcore.MMOClient;
import com.l2jprime.netcore.MMOConnection;
import com.l2jprime.netcore.SendablePacket;
import com.l2jprime.util.random.Rnd;

/**
 * Represents a client connected into the LoginServer
 * @author ProGramMoS
 */
public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>>
{
	private static Logger LOGGER = Logger.getLogger(L2LoginClient.class);

	public static enum LoginClientState
	{
		CONNECTED,
		AUTHED_GG,
		AUTHED_LOGIN
	}

	private LoginClientState _state;

	// Crypt
	private final LoginCrypt _loginCrypt;
	private final ScrambledKeyPair _scrambledPair;
	private final byte[] _blowfishKey;

	private String _account = "";
	private int _accessLevel;
	private int _lastServer;
	private boolean _usesInternalIP;
	private SessionKey _sessionKey;
	private final int _sessionId;
	private boolean _joinedGS;
	private final String _ip;
	private long _connectionStartTime;

	/**
	 * @param con
	 */
	public L2LoginClient(final MMOConnection<L2LoginClient> con)
	{
		super(con);
		_state = LoginClientState.CONNECTED;
		final String ip = getConnection().getInetAddress().getHostAddress();
		_ip = ip;
		final String[] localip = Config.NETWORK_IP_LIST.split(";");
		for (final String oneIp : localip)
		{
			if (ip.startsWith(oneIp) || ip.startsWith("127.0"))
			{
				_usesInternalIP = true;
			}
		}

		_scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		_blowfishKey = LoginController.getInstance().getBlowfishKey();
		_sessionId = Rnd.nextInt(Integer.MAX_VALUE);
		_connectionStartTime = System.currentTimeMillis();
		_loginCrypt = new LoginCrypt();
		_loginCrypt.setKey(_blowfishKey);
		LoginController.getInstance().addLoginClient(this);
		// This checkup must go next to BAN because it can cause decrease ban account time
		if (!BruteProtector.canLogin(ip))
		{
			LoginController.getInstance().addBanForAddress(getConnection().getInetAddress(), Config.BRUT_BAN_IP_TIME * 1000);
			LOGGER.warn("Drop connection from IP " + ip + " because of BruteForce.");
		}
		// Closer.getInstance().add(this);
	}

	public String getIntetAddress()
	{
		return _ip;
	}

	public boolean usesInternalIP()
	{
		return _usesInternalIP;
	}

	@Override
	public boolean decrypt(final ByteBuffer buf, final int size)
	{
		boolean ret = false;
		try
		{
			ret = _loginCrypt.decrypt(buf.array(), buf.position(), size);
			_connectionStartTime = System.currentTimeMillis();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			super.getConnection().close((SendablePacket<L2LoginClient>) null);
			return false;
		}

		if (!ret)
		{
			byte[] dump = new byte[size];
			System.arraycopy(buf.array(), buf.position(), dump, 0, size);
			LOGGER.warn("Wrong checksum from client: " + toString());
			super.getConnection().close((SendablePacket<L2LoginClient>) null);
			dump = null;
		}

		return ret;
	}

	@Override
	public boolean encrypt(final ByteBuffer buf, int size)
	{
		final int offset = buf.position();
		try
		{
			size = _loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return false;
		}

		buf.position(offset + size);
		return true;
	}

	public LoginClientState getState()
	{
		return _state;
	}

	public void setState(final LoginClientState state)
	{
		_state = state;
	}

	public byte[] getBlowfishKey()
	{
		return _blowfishKey;
	}

	public byte[] getScrambledModulus()
	{
		return _scrambledPair._scrambledModulus;
	}

	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair._pair.getPrivate();
	}

	public String getAccount()
	{
		return _account;
	}

	public void setAccount(final String account)
	{
		_account = account;
	}

	public void setAccessLevel(final int accessLevel)
	{
		_accessLevel = accessLevel;
	}

	public int getAccessLevel()
	{
		return _accessLevel;
	}

	public void setLastServer(final int lastServer)
	{
		_lastServer = lastServer;
	}

	public int getLastServer()
	{
		return _lastServer;
	}

	public int getSessionId()
	{
		return _sessionId;
	}

	public boolean hasJoinedGS()
	{
		return _joinedGS;
	}

	public void setJoinedGS(final boolean val)
	{
		_joinedGS = val;
	}

	public void setSessionKey(final SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}

	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}

	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}

	public void sendPacket(final L2LoginServerPacket lsp)
	{
		if (Config.DEBUG_PACKETS)
		{

			Log.add("[ServerPacket] SendingLoginServerPacket, Client: " + toString() + " Packet:" + lsp.getType(), "LoginServerPacketsLog");

		}

		getConnection().sendPacket(lsp);
	}

	public void close(final LoginFailReason reason)
	{
		getConnection().close(new LoginFail(reason));
	}

	public void close(final PlayFailReason reason)
	{
		getConnection().close(new PlayFail(reason));
	}

	public void close(final L2LoginServerPacket lsp)
	{
		getConnection().close(lsp);
	}

	@Override
	public void onDisconnection()
	{
		// Closer.getInstance().close(this);
		if (Config.DEBUG)
		{
			LOGGER.info("DISCONNECTED: " + toString());
		}

		LoginController.getInstance().removeLoginClient(this);
		if (!hasJoinedGS() && (getAccount() != null))
		{
			LoginController.getInstance().removeAuthedLoginClient(getAccount());
		}
	}

	@Override
	public String toString()
	{
		final InetAddress address = getConnection().getInetAddress();
		if (getState() == LoginClientState.AUTHED_LOGIN)
		{
			return "[" + getAccount() + " (" + (address == null ? "disconnected" : address.getHostAddress()) + ")]";
		}
		return "[" + (address == null ? "disconnected" : address.getHostAddress()) + "]";
	}

	@Override
	protected void onForcedDisconnection(final boolean critical)
	{
		// Empty
	}
}
