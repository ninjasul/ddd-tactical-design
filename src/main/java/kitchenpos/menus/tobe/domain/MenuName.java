package kitchenpos.menus.tobe.domain;

import java.util.Objects;

import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MenuName {
	public static final String NULL_OR_EMPTY_NAME_ERROR = "메뉴 이름은 비워 둘 수 없습니다.";

	@Column(name = "name", nullable = false)
	private String value;

	protected MenuName() {
	}

	public MenuName(String value) {
		if (!StringUtils.hasText(value)) {
			throw new IllegalArgumentException(NULL_OR_EMPTY_NAME_ERROR);
		}

		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;
		MenuName menuName = (MenuName)object;
		return Objects.equals(value, menuName.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}